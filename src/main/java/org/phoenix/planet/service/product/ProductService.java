package org.phoenix.planet.service.product;

import java.util.List;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;

public interface ProductService {

    List<EcoProductListResponse> getTodayEcoProductList();
}
