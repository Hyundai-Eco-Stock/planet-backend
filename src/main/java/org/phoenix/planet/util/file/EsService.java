package org.phoenix.planet.util.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EsService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.elasticsearch.uris}")
    private String baseUrl;

    // 인덱스 분리: knn(유사상품) / 검색(오타보정)
    private String knnIndex = "planet_product_v4";
    private String searchIndex = "planet_product_csv_3_pn_001";

    @Value("${es.default-size:20}")
    private int defaultSize;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    // 유사 상품 추천
    private static final String searchSimilarIdsQuery = """
            {
              "_source": ["productId","productName","brandName","categoryName","categoryId","imageUrl"],
              "size": %d,
              "knn": {
                "field": "productVector",
                "query_vector": %s,
                "k": %d,
                "num_candidates": 400,
                "filter": { "term": { "categoryId": %s } }
              },
              "query": {
                "bool": {
                  "must_not": [ { "term": { "productId": %s } } ]
                }
              },
              "sort": ["_score"]
            }
            """;

    // 검색 오타 보정
    private static final String searchMltMatchAllQuery = """
            {
              "size": %d,
              "_source": ["productId","productName","brandName","categoryName","categoryId","imageUrl"],
              "query": {
                "bool": {
                  "filter": [ %s ],
                  "must": { "match_all": {} },
                  "should": [
                    { "term": { "productName.keyword": { "value": %s, "boost": 8 } } },
                    { "match_phrase": { "productName": { "query": %s, "slop": 1, "boost": 5 } } },
                    { "match": { "productName": { "query": %s, "operator": "OR", "minimum_should_match": "60%%", "fuzziness": "AUTO", "fuzzy_transpositions": true, "prefix_length": 0, "max_expansions": 50, "lenient": true } } },
                    { "multi_match": { "query": %s, "type": "best_fields", "fields": ["productName^3","brandName^1.5","categoryName"], "operator": "OR", "minimum_should_match": "70%%", "fuzziness": "AUTO", "fuzzy_transpositions": true, "prefix_length": 1, "max_expansions": 50, "lenient": true, "auto_generate_synonyms_phrase_query": true } },
                    { "match_phrase_prefix": { "productName": { "query": %s, "max_expansions": 50, "boost": 1 } } },
                    { "fuzzy": { "productName": { "value": %s, "fuzziness": 2, "max_expansions": 100, "prefix_length": 0, "transpositions": true, "boost": 4 } } },
                    { "match_bool_prefix": { "productName": { "query": %s, "boost": 2 } } },
                    { "more_like_this": { "fields": ["productName","brandName"], "like": [ %s ], "min_term_freq": 1, "min_doc_freq": 1, "max_query_terms": 50 } }
                  ],
                  "minimum_should_match": 1
                }
              },
              "rescore": [
                {
                  "window_size": 200,
                  "query": {
                    "rescore_query": {
                      "more_like_this": {
                        "fields": ["productName","brandName"],
                        "like": [ %s ],
                        "min_term_freq": 1,
                        "min_doc_freq": 1,
                        "max_query_terms": 50
                      }
                    },
                    "query_weight": 0.2,
                    "rescore_query_weight": 2.0
                  }
                }
              ],
              "sort": ["_score"]
            }
            """;

    /* 유사 상품 추천 */
    public List<String> searchSimilarIds(String anchorName, String anchorCategoryId,
            String anchorId, Integer size) {
        int k = (size == null || size <= 0) ? defaultSize : size;
        String idx = this.knnIndex;

        if (k < 7) {
            k = 7; // 최소 7개 보장
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = java.util.Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());
        headers.set("Authorization", "Basic " + auth);

        // 1) 앵커 문서에서 productVector 가져오기 (ES 8.11.x: query_vector_builder.indexed 미지원)
        JsonNode vecArr = null;

        // 1) 우선 ES 문서 _id로 직접 조회 (성공 시 바로 사용)
        try {
            String getUrl =
                    baseUrl + "/" + idx + "/_doc/" + anchorId + "?_source_includes=productVector";
            ResponseEntity<String> getResp = restTemplate.exchange(
                    getUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            String getBody = getResp.getBody();
            JsonNode getNode =
                    (getBody == null || getBody.isBlank()) ? null : objectMapper.readTree(getBody);
            vecArr = (getNode == null) ? null : getNode.at("/_source/productVector");
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound nf) {
            // 2) _id로는 못 찾은 경우: productId로 1건 검색 후 productVector 추출
            try {
                String searchBody = String.format(
                        "{ \"_source\": [\"productVector\",\"productId\"], \"size\": 1, " +
                                "  \"query\": { \"bool\": { \"filter\": [ { \"term\": { \"productId\": %s } } ] } } }",
                        safeJson(anchorId)
                );
                HttpEntity<String> sreq = new HttpEntity<>(searchBody, headers);
                ResponseEntity<String> sres = restTemplate.exchange(
                        baseUrl + "/" + idx + "/_search",
                        HttpMethod.POST,
                        sreq,
                        String.class
                );
                String sBody = sres.getBody();
                JsonNode sNode =
                        (sBody == null || sBody.isBlank()) ? null : objectMapper.readTree(sBody);
                JsonNode hits = (sNode == null) ? null : sNode.at("/hits/hits");
                if (hits != null && hits.isArray() && hits.size() > 0) {
                    vecArr = hits.get(0).at("/_source/productVector");
                }
            } catch (Exception ignored) {
                // 무시하고 아래 공통 처리
            }
        } catch (Exception ex) {
            // 기타 예외는 유사 추천을 빈 결과로 처리
            return Collections.emptyList();
        }

        if (vecArr == null || !vecArr.isArray() || vecArr.size() == 0) {
            return Collections.emptyList();
        }
        // JSON 배열 문자열(따옴표 없이 그대로)로 사용
        String queryVectorJson = vecArr.toString();

        String query = String.format(
                searchSimilarIdsQuery,
                k,                       // size
                queryVectorJson,         // knn.query_vector (raw JSON array)
                k,                       // knn.k
                safeJson(anchorCategoryId), // knn.filter.categoryId
                safeJson(anchorId)       // must_not productId
        );

        try {
            HttpEntity<String> req = new HttpEntity<>(query, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/" + idx + "/_search",
                    HttpMethod.POST,
                    req,
                    String.class
            );
            String bodyStr = response.getBody();
            JsonNode resp =
                    (bodyStr == null || bodyStr.isBlank()) ? null : objectMapper.readTree(bodyStr);
            if (resp == null || resp.get("hits") == null) {
                return Collections.emptyList();
            }
            return toIds(resp);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /* 검색 */
    public List<String> searchMltMatchAll(String likeText, String categoryId, Integer size) {
        int k = (size == null || size <= 0) ? 10 : size;
        String idx = this.searchIndex;

        // 카테고리 필터 (카테고리 필터가 있는 경우 추가할 쿼리)
        String catTerm = "";
        if (categoryId != null && !categoryId.isBlank()) {
            catTerm = String.format("{ \"term\": { \"categoryId\": %s } }", safeJson(categoryId));
        }

        String body = String.format(searchMltMatchAllQuery,
                k,
                catTerm,
                safeJson(likeText),  // term exact boost
                safeJson(likeText),  // match_phrase
                safeJson(likeText),  // match (fuzzy)
                safeJson(likeText),  // multi_match (fuzzy)
                safeJson(likeText),  // match_phrase_prefix
                safeJson(likeText),  // fuzzy query
                safeJson(likeText),  // match_bool_prefix
                safeJson(likeText),  // MLT (should)
                safeJson(likeText)   // MLT (rescore)
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = java.util.Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes());
            headers.set("Authorization", "Basic " + auth);
            HttpEntity<String> req = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/" + idx + "/_search",
                    HttpMethod.POST,
                    req,
                    String.class
            );
            String bodyStr = response.getBody();
            JsonNode resp =
                    (bodyStr == null || bodyStr.isBlank()) ? null : objectMapper.readTree(bodyStr);
            if (resp == null || resp.get("hits") == null) {
                return Collections.emptyList();
            }
            return toIds(resp);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /* es 에 요청 보낼 때 json 으로 키워드 파싱 */
    private String safeJson(String s) {
        try {
            return objectMapper.writeValueAsString(s == null ? "" : s);
        } catch (Exception e) {
            return "\"\"";
        }
    }

    /* es 에서 받은 반환값을 List<String> 형태로 파싱 */
    private List<String> toIds(JsonNode resp) {
        JsonNode hits = resp.path("hits").path("hits");
        if (!hits.isArray()) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>();
        for (JsonNode h : hits) {
            JsonNode source = h.path("_source");
            String id = Optional.ofNullable(source.path("productId").asText(null))
                    .orElse(Optional.ofNullable(source.path("product_id").asText(null))
                            .orElse(Optional.ofNullable(source.path("id").asText(null))
                                    .orElse(null)));
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }
}
