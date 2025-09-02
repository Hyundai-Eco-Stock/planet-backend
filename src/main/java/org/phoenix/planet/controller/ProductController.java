package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.ProductDetailResponse;
import org.phoenix.planet.dto.product.response.ProductResponse;
import org.phoenix.planet.service.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /* 유사 상품 추천 */
    @GetMapping("/recommend")
    public ResponseEntity<List<ProductResponse>> recommend(RecommendRequest req) {

        return ResponseEntity.ok(productService.recommend(req));
    }

    /* 카테고리별 상품 조회 */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findByCategory(
        @RequestParam(required = false) Long categoryId) {

        return ResponseEntity.ok(productService.findByCategory(categoryId));
    }

    /* 카테고리 목록 조회 */
    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategory>> getCategories() {

        return ResponseEntity.ok(productService.getCategories());
    }

    /* 상품 검색 */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(
        @RequestParam(required = false) String categoryId,
        @RequestParam String searchKeyword) {

        return ResponseEntity.ok(productService.searchByMlt(searchKeyword, categoryId, 30));
    }

    /* 상품 상세 */
    @GetMapping("/{product-id}")
    public ResponseEntity<List<ProductDetailResponse>> getProductDetail(
        @PathVariable("product-id") long productId) {

        return ResponseEntity.ok(productService.getProductDetail(productId));
    }
}
