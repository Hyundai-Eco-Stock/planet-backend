package org.phoenix.planet.service.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.raw.ProductDoc;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsService {

    private final ElasticsearchClient esClient;

    private final String index = "planet_product_new";
    private final int defaultSize = 10;

    public List<String> searchMltMatchAll(String likeText, String categoryId, Integer size) {

        int k = (size == null || size <= 0) ? defaultSize : size;

        // 카테고리 필터 (있으면 term 추가)
        Query categoryFilter = (categoryId != null && !categoryId.isBlank())
            ? Query.of(q -> q.term(t -> t.field("categoryId").value(categoryId)))
            : null;

        // should 절
        List<Query> shouldQueries = List.of(
            Query.of(q -> q.term(t -> t.field("productName.keyword").value(likeText).boost(8.0f))),
            Query.of(q -> q.matchPhrase(m -> m.field("productName").query(likeText).boost(5.0f))),
            Query.of(q -> q.match(m -> m.field("productName").query(likeText)
                .operator(Operator.And)
                .minimumShouldMatch("100%")
                .boost(3.0f))),
            Query.of(q -> q.multiMatch(mm -> mm.query(likeText)
                .fields("productName^2", "brandName")
                .fuzziness("AUTO")
                .boost(1.0f))),
            Query.of(q -> q.matchPhrasePrefix(mpp -> mpp.field("productName")
                .query(likeText)
                .maxExpansions(50)
                .boost(1.0f)))
        );

        // 최종 query
        Query boolQuery = Query.of(q -> q.bool(b -> {
            if (categoryFilter != null) {
                b.filter(categoryFilter);
            }
            b.must(m -> m.matchAll(ma -> ma));
            b.should(shouldQueries);
            return b;
        }));

        // SearchRequest 생성
        SearchRequest request = SearchRequest.of(s -> s
            .index(index)
            .size(k)
            .query(boolQuery)
            .sort(SortOptions.of(so -> so.score(sc -> sc)))
            .source(src -> src.filter(f -> f.includes(
                "productId", "productName", "brandName",
                "categoryName", "categoryId", "imageUrl"
            )))
        );

        // 실행
        try {
            SearchResponse<ProductDoc> response = esClient.search(request, ProductDoc.class);
            return response.hits().hits().stream()
                .map(h -> h.source().productId())
                .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("[searchMltMatchAll]: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 유사 상품 추천 (more_like_this + filter + must_not)
     */
    public List<String> searchSimilarIds(
        String anchorName,
        String anchorCategoryId,
        String anchorId,
        Integer size
    ) {

        int k = (size == null || size <= 0) ? defaultSize : size;

        // filter: categoryId
        Query categoryFilter = (anchorCategoryId != null && !anchorCategoryId.isBlank())
            ? Query.of(q -> q.term(t -> t.field("categoryId").value(anchorCategoryId)))
            : null;

        // must_not: productId
        Query mustNot = (anchorId != null && !anchorId.isBlank())
            ? Query.of(q -> q.term(t -> t.field("productId").value(anchorId)))
            : null;

        // 요청 빌드 (rescore with more_like_this)
        SearchRequest request = SearchRequest.of(s -> {
            s.index(index)
                .size(k)
                .source(src -> src.filter(f -> f.includes(
                    "productId", "productName", "brandName",
                    "categoryName", "categoryId", "imageUrl"
                )))
                .query(q -> q.bool(b -> {
                    if (categoryFilter != null) {
                        b.filter(categoryFilter);
                    }
                    if (mustNot != null) {
                        b.mustNot(mustNot);
                    }
                    return b;
                }))
                .rescore(r -> r
                    .windowSize(200)
                    .query(rq -> rq
                        .query(Query.of(q -> q.moreLikeThis(mlt -> {
                            mlt.fields("productName")
                                .minTermFreq(1)
                                .minDocFreq(1)
                                .maxQueryTerms(50);

                            if (anchorId != null && !anchorId.isBlank()) {
                                mlt.like(l -> l.document(d -> d.index(index).id(anchorId)));
                            } else if (anchorName != null && !anchorName.isBlank()) {
                                mlt.like(l -> l.text(anchorName));
                            }
                            return mlt;
                        })))
                        .queryWeight(0.2)
                        .rescoreQueryWeight(2.0)
                    )
                );
            return s;
        });

        try {
            SearchResponse<ProductDoc> response = esClient.search(request, ProductDoc.class);
            return response.hits().hits().stream()
                .map(h -> h.source().productId())
                .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("[searchSimilarIds]: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}