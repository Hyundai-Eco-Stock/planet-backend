package org.phoenix.planet.dto.product.raw;

/**
 * 주문 처리 과정에서 사용되는 상품 정보
 */
public record ProductInfo(
        Long productId,
        String name,
        Long price,
        Integer availableQuantity,
        String ecoDealStatus,
        Integer salePercent
) {
}
