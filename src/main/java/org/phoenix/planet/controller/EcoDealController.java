package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.response.EcoProductDetailResponse;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.service.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/eco-products")
public class EcoDealController {

    private final ProductService productService;

    @GetMapping("/today")
    public ResponseEntity<List<EcoProductListResponse>> searchTodayEcoDealProducts() {
        return ResponseEntity.ok(productService.getTodayEcoProductList());
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<List<EcoProductDetailResponse>> getEcoDealDetail(
            @PathVariable("product-id") Long productId) {
        return ResponseEntity.ok(productService.getEcoDealDetail(productId));
    }
}
