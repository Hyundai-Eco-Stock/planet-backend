package org.phoenix.planet.service.order;

import org.phoenix.planet.dto.order.raw.OrderValidationResult;
import org.phoenix.planet.dto.order.request.OrderProductRequest;

import java.util.List;

public interface OrderValidationService {

    OrderValidationResult validateAndCalculate(List<OrderProductRequest> products, Long departmentStoreId);

}
