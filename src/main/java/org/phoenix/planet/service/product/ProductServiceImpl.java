package org.phoenix.planet.service.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public List<EcoProductListResponse> getTodayEcoProductList() {

        return productMapper.findTodayAllEcoProducts();
    }
}
