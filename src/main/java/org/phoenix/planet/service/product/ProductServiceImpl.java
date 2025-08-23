package org.phoenix.planet.service.product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.dto.product.response.ProductDetailResponse;
import org.phoenix.planet.dto.product.response.ProductResponse;
import org.phoenix.planet.mapper.ProductMapper;
import org.phoenix.planet.util.file.EsClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final EsClient esClient;
    private final ProductMapper productMapper;

    @Override
    public List<EcoProductListResponse> getTodayEcoProductList() {

        return productMapper.findTodayAllEcoProducts();
    }

    @Override
    public List<ProductCategory> getCategories() {
        List<ProductCategory> list = productMapper.findAllCategories();
        list.addFirst(ProductCategory.builder().name("전체").build()); // '전체' 카테고리 추가
        return list;
    }

    @Override
    public List<ProductResponse> findByCategory(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    /* 검색 */
    @Override
    public List<ProductResponse> searchByMlt(String keyword, String categoryId, Integer size) {
        // 1) ES 에서 추천 id 받기
        List<String> ids = esClient.searchMltMatchAll(keyword.trim(), categoryId, size);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 2) DB 에서 상품 상세 로드
        List<ProductResponse> fromDb = productMapper.findByIdIn(ids);
        // 3) ES 순서대로 상품 목록 정렬
        Map<Long, Integer> esProductOrder = IntStream.range(0, ids.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> Long.parseLong(ids.get(i)), // productId
                        i -> i                           // 순서
                ));
        fromDb.sort(Comparator.comparingInt(
                p -> esProductOrder.getOrDefault(p.getProductId(), Integer.MAX_VALUE)));
        return fromDb;
    }

    /* 상품 상세 */
    @Override
    public List<ProductDetailResponse> getProductDetail(Long productId) {
        return productMapper.getProductDetail(productId);
    }

    /* 유사 상품 추천 */
    @Override
    public List<ProductResponse> recommend(RecommendRequest req) {
        // 1) ES에서 추천 id만 받기
        List<String> ids = esClient.searchSimilarIds(
                req.name(),
                req.categoryId(),
                req.id(),
                req.size()
        );
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) Oracle에서 상세 조회
        List<ProductResponse> fromDb = productMapper.findByIdIn(ids);
        if (fromDb.isEmpty()) {
            return fromDb;
        }

        // 3) ES 순서대로 정렬 (ES ids는 String, DB id는 Long/Integer일 수 있으므로 String 기준으로 맞춤)
        Map<String, Integer> rank = IntStream.range(0, ids.size())
                .boxed()
                .collect(Collectors.toMap(
                        ids::get,
                        i -> i,
                        (a, b) -> a // 중복 id가 있을 경우 첫 위치 유지
                ));

        fromDb.sort(Comparator.comparingInt(p -> {
            String key = (p == null) ? "" : String.valueOf(p.getProductId());
            return rank.getOrDefault(key, Integer.MAX_VALUE);
        }));

        return fromDb;
    }
}
