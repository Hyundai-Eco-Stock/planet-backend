package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.response.ProductResponse;
import org.phoenix.planet.service.product.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /* 유사 상품 추천 (아직 사용 x) */
//    @GetMapping("/recommend")
//    public RecommendResponse recommend(RecommendRequest req) {
//        List<Product> products = productService.recommend(req);
//        return RecommendResponse.builder()
//                .products(products)
//                .build();
//    }

    /* 카테고리별 상품 조회 */
    @GetMapping
    public List<ProductResponse> findByCategory(@RequestParam(required = false) Long categoryId) {
        return productService.findByCategory(categoryId);
    }

    /* 카테고리 목록 조회 */
    @GetMapping("/categories")
    public List<ProductCategory> getCategories() {
        return productService.getCategories();
    }

    /* 상품 검색 */
    @GetMapping("/search")
    public List<Product> search(@RequestParam(required = false) String categoryId,
            @RequestParam String searchKeyword) {
        return productService.searchByMlt(searchKeyword, categoryId, 30);
    }
}
