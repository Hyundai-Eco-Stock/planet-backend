package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.product.raw.ProductCategory;

@Mapper
public interface ProductCategoryMapper {

    int insert(ProductCategory category);

    int update(ProductCategory category);

    int delete(Long categoryId);

    ProductCategory findById(Long categoryId);

    List<ProductCategory> findAll();

    ProductCategory findByName(String name);
}