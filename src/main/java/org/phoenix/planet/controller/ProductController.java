package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.RecommendResponse;
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

    @GetMapping("/recommend")
    public RecommendResponse recommend(RecommendRequest req) {
        log.info("들어왔다 !!");
        List<Product> products = productService.recommend(req);
        return RecommendResponse.builder()
                .products(products)
                .build();
    }

    // 카테고리, 상품목록 분리하고 초기 렌더링 시에만 카테고리 api 호출
    @GetMapping
    public List<Product> findByCategory(@RequestParam(required = false) Long categoryId) {
        return productService.findByCategory(categoryId);
    }

    @GetMapping("/categories")
    public List<ProductCategory> getCategories() {
        List<ProductCategory> dto = productService.getCategories();
        log.info(dto.get(1).toString());
        return dto;
    }

    /* 검색 */
    @GetMapping("/search")
    public List<Product> search(@RequestParam(required = false) String categoryId,
            @RequestParam String searchKeyword) {
        log.info("GET /products/search?searchKeyword={} called", searchKeyword);
        return productService.searchByMlt(searchKeyword, categoryId, 10);
    }
}
