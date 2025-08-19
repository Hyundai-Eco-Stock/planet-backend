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

        // 3) ES 순서대로 정렬
        Map<String, Integer> rank = IntStream.range(0, ids.size())
                .boxed().collect(Collectors.toMap(ids::get, i -> i));
        fromDb.sort(Comparator.comparingInt(p -> rank.getOrDefault(p.getId(), Integer.MAX_VALUE)));

        return fromDb;
    }
}
