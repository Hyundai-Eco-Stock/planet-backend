package org.phoenix.planet.service.product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
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
        list.addFirst(
                ProductCategory.builder()
                        .name("전체")
                        .build()
        );
        return list;
    }

    @Override
    public List<Product> findByCategory(Long categoryId) {
        log.info("findByCategory called with category: {}", categoryId);
        return productMapper.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> recommend(RecommendRequest req) {
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
        List<Product> fromDb = productMapper.findByIdIn(ids);
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
            String key = (p == null) ? "" : String.valueOf(p.getId()); // getId() is primitive long
            return rank.getOrDefault(key, Integer.MAX_VALUE);
        }));

        return fromDb;
    }

    /* 검색 */
    @Override
    public List<Product> searchByMlt(String keyword, String categoryId, Integer size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 1) ES에서 추천 id만 받기 (MLT)
        List<String> ids = esClient.searchMltMatchAll(keyword.trim(), categoryId, size);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        for (String id : ids) {
            log.info("추천 상품 ID: {}", id);
        }

        // 2) DB 상세 로드
        List<Product> fromDb = productMapper.findByIdIn(ids);
        if (fromDb.isEmpty()) {
            return fromDb;
        }

        // 3) ES 순서대로 정렬 (ES ids는 String, DB id는 Long/Integer일 수 있으므로 String 기준으로 맞춤)
        Map<String, Integer> rank = IntStream.range(0, ids.size())
                .boxed()
                .collect(Collectors.toMap(
                        ids::get,
                        i -> i,
                        (a, b) -> a
                ));

        fromDb.sort(Comparator.comparingInt(p -> {
            String key = (p == null) ? "" : String.valueOf(p.getId()); // getId() is primitive long
            return rank.getOrDefault(key, Integer.MAX_VALUE);
        }));

        for (Product p : fromDb) {
            log.info("DB에서 조회된 이후 ID: {}", p.getId());
        }

        return fromDb;


    }
}
