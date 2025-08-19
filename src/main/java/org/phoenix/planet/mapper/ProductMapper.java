package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.dto.product.response.ProductCategoryDto;
import org.phoenix.planet.dto.product.response.ProductDto;

@Mapper
public interface ProductMapper {

    int insert(Product product);

    int update(Product product);

    int delete(Long productId);

    Product findById(Long productId);

    List<Product> findAll();

    List<EcoProductListResponse> findTodayAllEcoProducts();

    List<Product> findByIdIn(List<String> ids);

    List<ProductDto> findByCategoryId(Long category);

    List<ProductCategoryDto> findAllCategories();
}
