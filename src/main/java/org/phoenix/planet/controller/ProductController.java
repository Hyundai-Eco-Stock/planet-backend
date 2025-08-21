package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.ProductCategoryResponse;
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

    @GetMapping
    public ProductCategoryResponse findByCategory(@RequestParam(required = false) Long categoryId) {
        log.info("GET /products?category={} called", categoryId);
        return productService.findByCategory(categoryId);
    }
}
