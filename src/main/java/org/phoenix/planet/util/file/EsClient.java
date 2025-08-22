package org.phoenix.planet.util.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class EsClient {

    private final WebClient webClient;
    private final ObjectMapper om = new ObjectMapper();

    private final String index;
    private final String inferenceId;
    private final int defaultSize;

    public EsClient(
            @Value("${es.endpoint}") String endpoint,
            @Value("${es.api-key}") String apiKey,
            @Value("${es.index}") String index,
            @Value("${es.inference-id}") String inferenceId,
            @Value("${es.default-size:20}") int defaultSize
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(endpoint)
                .defaultHeader("Authorization", "ApiKey " + apiKey) // Elastic Cloud API 키
                .build();
        this.index = index;
        this.inferenceId = inferenceId;
        this.defaultSize = defaultSize;
    }

    /* 유사 상품 추천 */
    public List<String> searchSimilarIds(String anchorName, String anchorCategoryId,
            String anchorId, Integer size) {
        int k = (size == null || size <= 0) ? defaultSize : size;

        // Helpers
        String idx = (this.index == null || this.index.isBlank()) ? "planet_product_1" : this.index;
        String likeText = safeJson(anchorName); // "\"상품명\"" 형태

        // category_id filter (numeric if possible)
        String catTerm = "";
        if (anchorCategoryId != null && !anchorCategoryId.isBlank()) {
            boolean catIsNumeric = anchorCategoryId.matches("-?\\d+");
            String catValue = catIsNumeric ? anchorCategoryId : safeJson(anchorCategoryId);
            catTerm = String.format("{ \"term\": { \"category_id\": %s } }", catValue);
        }

        // self exclusion (product_id) - numeric if possible
        String mustNotTerm = "";
        if (anchorId != null && !anchorId.isBlank()) {
            boolean idIsNumeric = anchorId.matches("-?\\d+");
            String idValue = idIsNumeric ? anchorId : safeJson(anchorId);
            mustNotTerm = String.format("{ \"term\": { \"product_id\": %s } }", idValue);
        }

        // rescore like-clause: prefer document by _id when available, else text
        String likeClause;
        if (anchorId != null && !anchorId.isBlank()) {
            // _index and _id must be strings in the like doc
            likeClause = String.format("{ \"_index\": %s, \"_id\": %s }", safeJson(idx),
                    safeJson(anchorId));
        } else {
            likeClause = likeText; // use raw text
        }

        String query = String.format("""
                {
                  "_source": ["product_id","product_name","brand_name","category_name","category_id","image_url"],
                  "size": %d,
                  "query": {
                    "bool": {
                      "filter": [ %s ],
                      "must_not": [ %s ]
                    }
                  },
                  "rescore": [
                    {
                      "window_size": 200,
                      "query": {
                        "rescore_query": {
                          "more_like_this": {
                            "fields": ["product_name"],
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
                  ]
                }
                """, k, catTerm, mustNotTerm, likeClause);

        JsonNode resp = webClient.post()
                .uri("/" + idx + "/_search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (resp == null || resp.get("hits") == null) {
            return Collections.emptyList();
        }
        return toIds(resp);
    }


    /* 검색 (기존 시그니처 유지: 카테고리 필터 없음) */
    public List<String> searchMltMatchAll(String likeText, Integer size) {
        return searchMltMatchAll(likeText, null, size);
    }

    /* 검색 (카테고리 필터 추가 버전) */
    public List<String> searchMltMatchAll(String likeText, String categoryId, Integer size) {
        int k = (size == null || size <= 0) ? 10 : size;
        String idx = (this.index == null || this.index.isBlank()) ? "planet_product_1" : this.index;

        // category_id filter (numeric if possible)
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
                              "must": { "match_all": {} }
                            }
                          },
                          "rescore": [
                            {
                              "window_size": 200,
                              "query": {
                                "rescore_query": {
                                  "more_like_this": {
                                    "fields": ["product_name","category_name"],
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
                          ]
                        }
                        """,
                k,
                catTerm,
                safeJson(likeText)
        );

        JsonNode resp = webClient.post()
                .uri("/" + idx + "/_search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (resp == null || resp.get("hits") == null) {
            return Collections.emptyList();
        }
        return toIds(resp);
    }

    private String safeJson(String s) {
        try {
            return om.writeValueAsString(s == null ? "" : s);
        } catch (Exception e) {
            return "\"\"";
        }
    }

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
