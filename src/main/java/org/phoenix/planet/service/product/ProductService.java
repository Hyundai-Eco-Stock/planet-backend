package org.phoenix.planet.service.product;

import java.util.List;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.request.RecommendRequest;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.mapper.ProductMapper;
import org.phoenix.planet.util.file.EsClient;
import org.springframework.stereotype.Service;

public interface ProductService {
    List<EcoProductListResponse> getTodayEcoProductList();
    List<Product> recommend(RecommendRequest req);
}
