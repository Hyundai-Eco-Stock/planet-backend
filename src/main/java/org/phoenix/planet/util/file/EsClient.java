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
public class EsClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${es.endpoint}")
    private String baseUrl;

    @Value("${es.index}")
    private String index;

    @Value("${es.inference-id}")
    private String inferenceId;

    @Value("${es.default-size:20}")
    private int defaultSize;

    @Value("${es.api-key}")
    private String apiKey;

    /* 유사 상품 추천 */
    public List<String> searchSimilarIds(String anchorName, String anchorCategoryId,
            String anchorId, Integer size) {
        int k = (size == null || size <= 0) ? defaultSize : size;

        String idx = (this.index == null || this.index.isBlank()) ? "planet_product_1" : this.index;

        String query = String.format(
                """
                        {
                          "_source": ["product_id","product_name","brand_name","category_name","category_id","image_url"],
                          "size": %d,
                          "query": {
                            "bool": {
                              "filter": [ { "term": { "category_id": %s } } ],
                              "must_not": [ { "term": { "product_id": %s } } ]
                            }
                          },
                          "rescore": [
                            {
                              "window_size": 200,
                              "query": {
                                "rescore_query": {
                                  "more_like_this": {
                                    "fields": ["product_name"],
                                    "like": [ { "_index": %s, "_id": %s } ],
                                    "min_term_freq": 1,
                                    "min_doc_freq": 1,
                                    "max_query_terms": 50
                                  }
                                },
                                "query_weight": 0.2,
                                "rescore_query_weight": 2.0
                              }
                            }
                          ]
                        }
                        """,
                k,
                safeJson(anchorCategoryId),
                safeJson(anchorId),
                safeJson(idx),
                safeJson(anchorId)
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "ApiKey " + apiKey);
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
        String idx = (this.index == null || this.index.isBlank()) ? "planet_product_1" : this.index;

        // 카테고리 필터 (카테고리 필터가 있는 경우 추가할 쿼리)
        String catTerm = "";
        if (categoryId != null && !categoryId.isBlank()) {
            boolean catIsNumeric = categoryId.matches("-?\\d+");
            String catValue = catIsNumeric ? categoryId : safeJson(categoryId);
            catTerm = String.format("{ \"term\": { \"category_id\": %s } }", catValue);
        }

        String body = String.format(
                """
                        {
                          "size": %d,
                          "_source": ["product_id","product_name","brand_name","category_name","category_id","image_url"],
                          "query": {
                            "bool": {
                              "filter": [ %s ],
                              "must": { "match_all": {} },
                              "should": [
                                { "term": { "product_name.keyword": { "value": %s, "boost": 8 } } },
                                { "match_phrase": { "product_name": { "query": %s, "boost": 5 } } },
                                { "match": { "product_name": { "query": %s, "operator": "AND", "minimum_should_match": "100%%", "boost": 3 } } },
                                { "multi_match": { "query": %s, "fields": ["product_name^2","brand_name"], "fuzziness": "AUTO", "boost": 1 } },
                                { "match_phrase_prefix": { "product_name": { "query": %s, "max_expansions": 50, "boost": 1 } } }
                              ]
                            }
                          },
                          "sort": ["_score"]
                        }
                        """,
                k,
                catTerm,
                safeJson(likeText),
                safeJson(likeText),
                safeJson(likeText),
                safeJson(likeText),
                safeJson(likeText)
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "ApiKey " + apiKey);
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
            String id = Optional.ofNullable(source.path("product_id").asText(null))
                    .orElse(Optional.ofNullable(source.path("id").asText(null)).orElse(null));
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }
}
