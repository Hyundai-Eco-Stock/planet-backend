package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.raw.ProductCategory;
import org.phoenix.planet.dto.product.response.EcoProductDetailResponse;
import org.phoenix.planet.dto.product.response.EcoProductListResponse;
import org.phoenix.planet.dto.product.response.ProductDetailResponse;
import org.phoenix.planet.dto.product.response.ProductResponse;

@Mapper
public interface ProductMapper {

    int insert(Product product);

    int update(Product product);

    int delete(Long productId);

    Product findById(Long productId);

    List<Product> findAll();

    List<EcoProductListResponse> findTodayAllEcoProducts();

    List<ProductResponse> findByIdIn(List<Long> ids);

    List<ProductResponse> findByCategoryId(Long category);

    List<ProductCategory> findAllCategories();

    List<Product> findByIds(List<Long> productIds);

    int deductStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    Integer getStock(@Param("productId") Long productId);

    List<ProductDetailResponse> getProductDetail(Long productId);

    int restoreStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    List<EcoProductDetailResponse> getEcoDealDetail(Long productId);

}
