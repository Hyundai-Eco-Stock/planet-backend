package org.phoenix.planet.service.order;

import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.order.raw.PriceCalculationResult;
import org.phoenix.planet.dto.order.request.OrderProductRequest;
import org.phoenix.planet.dto.product.raw.ProductInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PriceCalculationServiceImpl implements PriceCalculationService {

    @Override
    public PriceCalculationResult calculatePrice(List<ProductInfo> productInfos,
                                                 List<OrderProductRequest> orderProducts,
                                                 Long usedPoint,
                                                 Long donationPrice) {
        Long originPrice = calculateOriginPrice(productInfos, orderProducts);
        Map<Long, Long> ecoDiscountMap = calculateEcoDiscount(productInfos, orderProducts);
        Long totalEcoDiscount = ecoDiscountMap.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        // 포인트/기부금은 주문서 화면에서 입력 X
        Long finalPayPrice = originPrice - totalEcoDiscount;

        return new PriceCalculationResult(
                originPrice,
                totalEcoDiscount,
                0L,
                0L,
                finalPayPrice,
                ecoDiscountMap
        );
    }

    private Long calculateOriginPrice(List<ProductInfo> productInfos, List<OrderProductRequest> orderProducts) {
        Long totalPrice = 0L;

        for (int i = 0; i < productInfos.size(); i++) {
            ProductInfo productInfo = productInfos.get(i);
            OrderProductRequest orderProduct = orderProducts.get(i);

            totalPrice += productInfo.price() * orderProduct.quantity();
        }

        return totalPrice;
    }

    private Map<Long, Long> calculateEcoDiscount(List<ProductInfo> productInfos,
                                                 List<OrderProductRequest> orderProducts) {
        Map<Long, Long> ecoDiscountMap = new HashMap<>();

        for (int i = 0; i < productInfos.size(); i++) {
            ProductInfo productInfo = productInfos.get(i);
            OrderProductRequest orderProduct = orderProducts.get(i);

            if (productInfo.ecoDealStatus() != null && productInfo.salePercent() != null && productInfo.salePercent() > 0) {
                Long productTotalPrice = productInfo.price() * orderProduct.quantity();

                // 할인율 적용 (소수점 버림)
                Long discountAmount = (long) (productTotalPrice * productInfo.salePercent() / 100.0);
                ecoDiscountMap.put(productInfo.productId(), discountAmount);

                log.debug("상품 {} 에코딜 할인 - 원가: {}, 할인율: {}%, 할인금액: {}",
                        productInfo.productId(), productTotalPrice, productInfo.salePercent(), discountAmount);
            } else {
                ecoDiscountMap.put(productInfo.productId(), 0L);
            }
        }

        return ecoDiscountMap;
    }

}
