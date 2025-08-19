package org.phoenix.planet.service.order;

import org.phoenix.planet.dto.order.raw.PriceCalculationResult;
import org.phoenix.planet.dto.order.request.OrderProductRequest;
import org.phoenix.planet.dto.product.raw.ProductInfo;

import java.util.List;

public interface PriceCalculationService {

    PriceCalculationResult calculatePrice(List<ProductInfo> productInfos,
                                          List<OrderProductRequest> orderProducts,
                                          Long usedPoint,
                                          Long donationPrice);

}
