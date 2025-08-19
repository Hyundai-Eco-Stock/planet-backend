package org.phoenix.planet.service.product;

import java.util.List;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.dto.product.response.ProductCategoryResponse;

public interface ProductService {

    List<EcoProductListResponse> getTodayEcoProductList();

    List<Product> recommend(RecommendRequest req);

    ProductCategoryResponse findByCategory(Long categoryId);
}
