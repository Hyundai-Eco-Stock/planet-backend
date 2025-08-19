package org.phoenix.planet.service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.CancelStatus;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.constant.OrderStatus;
import org.phoenix.planet.constant.OrderType;
import org.phoenix.planet.dto.order.raw.OrderHistory;
import org.phoenix.planet.dto.order.raw.OrderProduct;
import org.phoenix.planet.dto.order.raw.PriceCalculationResult;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.request.OrderProductRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.dto.product.raw.ProductInfo;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.mapper.OrderMapper;
import org.phoenix.planet.mapper.ProductMapper;
import org.phoenix.planet.util.order.OrderNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final PriceCalculationService priceCalculationService;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, Long memberId) {
        try {
            List<ProductInfo> productInfos = getProducts(request.products());  // 상품 정보 조회

            checkStock(request.products(), productInfos);  // 재고 확인
            checkOrderType(request);  // 배송 유형 확인
            checkPickupProducts(request.products(), productInfos);

            String orderNumber = OrderNumberGenerator.generate();  // 주문 번호 생성
            PriceCalculationResult priceResult = priceCalculationService.calculatePrice(
                    productInfos, request.products(), request.usedPoint(), request.donationPrice());

            saveOrderData(orderNumber, memberId, request, priceResult, productInfos);

            return new CreateOrderResponse(
                    orderNumber,
                    priceResult.finalPayPrice(),
                    "주문서가 성공적으로 생성되었습니다."
            );
        } catch (OrderException e) {
            log.error("주문서 생성 실패 - 회원ID: {}, 오류: {}", memberId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("주문서 생성 중 예상치 못한 오류 - 회원ID: {}", memberId, e);
            throw new OrderException(OrderError.ORDER_CREATION_FAILED);
        }
    }

    private List<ProductInfo> getProducts(List<OrderProductRequest> orderProducts) {
        List<ProductInfo> productInfos = new ArrayList<>();

        for (OrderProductRequest orderProduct : orderProducts) {
            Product product = productMapper.findById(orderProduct.productId());

            if (product == null) {
                throw new OrderException(OrderError.PRODUCT_NOT_FOUND);
            }

            ProductInfo productInfo = new ProductInfo(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getEcoDealStatus(),
                    product.getSalePercent()
            );

            productInfos.add(productInfo);
        }

        return productInfos;
    }

    private void checkStock(List<OrderProductRequest> orderProducts, List<ProductInfo> productInfos) {
        List<String> insufficientProducts = new ArrayList<>();

        for (int i = 0; i < orderProducts.size(); i++) {
            OrderProductRequest orderProduct = orderProducts.get(i);
            ProductInfo productInfo = productInfos.get(i);

            if (orderProduct.quantity() > productInfo.availableQuantity()) {
                insufficientProducts.add(productInfo.name());
            }
        }

        if (!insufficientProducts.isEmpty()) {
            log.warn("재고 부족 상품들: {}", insufficientProducts);
            throw new OrderException(OrderError.INSUFFICIENT_STOCK);
        }
    }

    private void checkOrderType(CreateOrderRequest request) {
        boolean hasPickup = request.products().stream()
                .anyMatch(p -> p.orderType() == OrderType.PICKUP);
        boolean hasDelivery = request.products().stream()
                .anyMatch(p -> p.orderType() == OrderType.DELIVERY);

        if (hasPickup && hasDelivery) {
            throw new OrderException(OrderError.MIXED_ORDER_TYPE);
        }

        if (hasPickup && request.departmentStoreId() == null) {
            throw new OrderException(OrderError.PICKUP_STORE_REQUIRED);
        }
    }

    private void checkPickupProducts(List<OrderProductRequest> orderProducts, List<ProductInfo> productInfos) {
        for (int i = 0; i < orderProducts.size(); i++) {
            OrderProductRequest orderProduct = orderProducts.get(i);
            ProductInfo productInfo = productInfos.get(i);

            if (orderProduct.orderType() == OrderType.PICKUP && productInfo.ecoDealStatus() == null) {
                throw new OrderException(OrderError.ECODEAL_PRODUCT_REQUIRED_FOR_PICKUP);
            } else if (orderProduct.orderType() == OrderType.DELIVERY && productInfo.ecoDealStatus() != null) {
                throw new OrderException(OrderError.DELIVERY_REQUIRES_REGULAR_PRODUCT);
            }
        }
    }

    private void saveOrderData(String orderNumber, Long memberId, CreateOrderRequest request,
                               PriceCalculationResult priceCalculationResult, List<ProductInfo> productInfos) {
        OrderHistory orderHistory = OrderHistory.builder()
                .orderNumber(orderNumber)
                .orderStatus(OrderStatus.PENDING)
                .originPrice(priceCalculationResult.originPrice())
                .usedPoint(request.usedPoint())
                .donationPrice(request.donationPrice())
                .finalPayPrice(priceCalculationResult.finalPayPrice())
                .ecoDealQrUrl(null)
                .memberId(memberId)
                .departmentStoreId(request.departmentStoreId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderMapper.insertOrderHistory(orderHistory);

        Long orderHistoryId = orderHistory.getOrderHistoryId();
        List<OrderProduct> orderProducts = createOrderProducts(request.products(), productInfos,
                priceCalculationResult.ecoDealDiscountMap(), orderHistoryId);

        for (OrderProduct orderProduct : orderProducts) {
            orderMapper.insertOrderProduct(orderProduct);
        }
    }

    private List<OrderProduct> createOrderProducts(List<OrderProductRequest> orderProductRequests,
                                                   List<ProductInfo> productInfos,
                                                   Map<Long, Long> ecoDealDiscountMap,
                                                   Long orderHistoryId) {
        List<OrderProduct> orderProducts = new ArrayList<>();

        for (int i = 0; i < orderProductRequests.size(); i++) {
            OrderProductRequest orderProduct = orderProductRequests.get(i);
            ProductInfo productInfo = productInfos.get(i);

            OrderProduct product = OrderProduct.builder()
                    .orderHistoryId(orderHistoryId)
                    .price(productInfo.price())
                    .quantity(orderProduct.quantity())
                    .cancelStatus(CancelStatus.N)
                    .orderType(orderProduct.orderType())
                    .productId(orderProduct.productId())
                    .ecoDealDiscount(ecoDealDiscountMap.getOrDefault(orderProduct.productId(), 0L))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            orderProducts.add(product);
        }

        return orderProducts;
    }

}
