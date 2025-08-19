package org.phoenix.planet.service.order;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.constant.OrderType;
import org.phoenix.planet.dto.order.raw.OrderValidationProduct;
import org.phoenix.planet.dto.order.raw.OrderValidationResult;
import org.phoenix.planet.dto.order.request.OrderProductRequest;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.mapper.DepartmentStoreProductMapper;
import org.phoenix.planet.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderValidationServiceImpl implements OrderValidationService {

    private final ProductMapper productMapper;
    private final DepartmentStoreProductMapper departmentStoreProductMapper;

    @Override
    public OrderValidationResult validateAndCalculate(List<OrderProductRequest> products, Long departmentStoreId) {
        List<OrderValidationProduct> validatedItems = new ArrayList<>();
        Long totalAmount = 0L;

        for (OrderProductRequest productRequest : products) {
            OrderValidationProduct item = validateSingleProduct(productRequest, departmentStoreId);
            validatedItems.add(item);
            totalAmount += item.totalPrice();
        }

        return new OrderValidationResult(validatedItems, totalAmount);
    }

    private OrderValidationProduct validateSingleProduct(OrderProductRequest productRequest, Long departmentStoreId) {
        // 상품 존재 여부 확인
        Product product = productMapper.findById(productRequest.productId());

        if (product == null) {
            throw new OrderException(OrderError.PRODUCT_NOT_FOUND);
        }

        // 재고 확인
        if (product.getQuantity() < productRequest.quantity()) {
            throw new OrderException(OrderError.INSUFFICIENT_STOCK);
        }

        boolean isEcoDeal = product.getEcoDealStatus() != null;
        OrderType requestOrderType = productRequest.orderType();

        if (isEcoDeal) {
            // ecoDeal 상품: 무조건 픽업만 가능
            if (requestOrderType != OrderType.PICKUP) {
                throw new OrderException(OrderError.ECODEAL_PRODUCT_PICKUP_ONLY);
            }

            // ecoDeal 픽업이면 매장 ID 필수
            if (departmentStoreId == null) {
                throw new OrderException(OrderError.PICKUP_STORE_REQUIRED);
            }

            // 해당 매장에서 상품 판매 여부 확인
            validateProductAvailableAtStore(productRequest.productId(), departmentStoreId);
        } else {
            // 일반 상품: 무조건 배송만 가능
            if (requestOrderType != OrderType.DELIVERY) {
                throw new OrderException(OrderError.NORMAL_PRODUCT_DELIVERY_ONLY);
            }

            // 일반 상품인데 매장 ID가 있으면 에러
            if (departmentStoreId != null) {
                throw new OrderException(OrderError.DELIVERY_STORE_NOT_ALLOWED);
            }
        }

        // 가격 계산
        Long unitPrice = product.getPrice();
        Long totalPrice = unitPrice * productRequest.quantity();

        return new OrderValidationProduct(
                productRequest.productId(),
                productRequest.quantity(),
                unitPrice,
                totalPrice,
                product.getName()
        );
    }

    private void validateProductAvailableAtStore(Long productId, Long departmentStoreId) {
        boolean isAvailable = departmentStoreProductMapper
                .existsByProductIdAndDepartmentStoreId(productId, departmentStoreId);

        if (!isAvailable) {
            throw new OrderException(OrderError.PRODUCT_NOT_AVAILABLE);
        }
    }

}
