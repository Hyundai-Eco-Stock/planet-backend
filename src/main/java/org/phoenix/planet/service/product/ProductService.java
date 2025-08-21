package org.phoenix.planet.service.product;

import java.util.List;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;

public interface ProductService {

    List<EcoProductListResponse> getTodayEcoProductList();

    List<Product> recommend(RecommendRequest req);

    List<ProductCategory> getCategories();

    List<Product> findByCategory(Long categoryId);

    // 상품 검색
    List<Product> searchByMlt(String keyword, Integer size);
}
