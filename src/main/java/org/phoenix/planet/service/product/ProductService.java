package org.phoenix.planet.service.product;

import java.util.List;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.EcoProductDetailResponse;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.dto.product.response.ProductDetailResponse;
import org.phoenix.planet.dto.product.response.ProductResponse;

public interface ProductService {

    List<EcoProductListResponse> getTodayEcoProductList();

    List<ProductResponse> recommend(RecommendRequest req);

    List<ProductCategory> getCategories();

    List<ProductResponse> findByCategory(Long categoryId);

    List<ProductResponse> searchByMlt(String keyword, String categoryId, Integer size);

    List<ProductDetailResponse> getProductDetail(Long productId);

    List<EcoProductDetailResponse> getEcoDealDetail(Long productId);
}
