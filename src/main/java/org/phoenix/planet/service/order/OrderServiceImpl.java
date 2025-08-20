package org.phoenix.planet.service.order;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.constant.AuthenticationError;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.constant.OrderType;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.dto.order.raw.OrderDraft;
import org.phoenix.planet.dto.order.raw.OrderValidationResult;
import org.phoenix.planet.dto.order.raw.PickupStoreInfo;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.request.OrderProductRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.order.response.OrderDraftProductResponse;
import org.phoenix.planet.dto.order.response.OrderDraftResponse;
import org.phoenix.planet.dto.product.raw.Product;
import org.phoenix.planet.error.auth.AuthException;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.mapper.DepartmentStoreMapper;
import org.phoenix.planet.mapper.DepartmentStoreProductMapper;
import org.phoenix.planet.mapper.MemberMapper;
import org.phoenix.planet.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderValidationService orderValidationService;
    private final OrderDraftService orderDraftService;
    private final OrderNumberService orderNumberService;
    private final DepartmentStoreProductMapper departmentStoreProductMapper;
    private final DepartmentStoreMapper departmentStoreMapper;
    private final MemberMapper memberMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, Long memberId) {
        // 상품 검증
        OrderValidationResult validationResult = orderValidationService.validateAndCalculate(request.products(), null);
        String orderNumber = orderNumberService.generateOrderNumber();

        // 사용자 보유 포인트 조회
        Long availablePoint = getMemberAvailablePoint(memberId);

        // 기본 기부금 계산
        Long defaultDonationPrice = calculateDefaultDonation(validationResult.totalAmount());

        // 픽업 가능한 매장 리스트 조회
        List<PickupStoreInfo> availablePickupStores = findCommonPickupStores(request.products());

        OrderDraft orderDraft = createOrderDraft(
                orderNumber,
                memberId,
                request.products(),
                validationResult.totalAmount(),  // 순수 상품 금액만
                null, // 매장 선택은 나중에
                availablePoint,
                defaultDonationPrice
        );
        orderDraftService.saveOrderDraft(orderDraft);

        return new CreateOrderResponse(
                orderNumber,
                validationResult.totalAmount(), // 상품 금액만 반환
                availablePickupStores,
                "주문서가 성공적으로 생성되었습니다."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDraftResponse getOrderDraft(String orderNumber, Long memberId) {
        // 주문번호 유효성 검증
        if (!orderNumberService.isValidOrderNumber(orderNumber)) {
            throw new OrderException(OrderError.ORDER_NOT_FOUND);
        }

        // OrderDraftService를 통해 Redis에서 조회
        OrderDraft orderDraft = orderDraftService.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderException(OrderError.ORDER_NOT_FOUND));

        // 권한 검증
        if (!orderDraft.memberId().equals(memberId)) {
            throw new OrderException(OrderError.UNAUTHORIZED_ORDER_ACCESS);
        }

        // 주문서 만료 여부 검증
        if (orderDraft.validUntil().isBefore(LocalDateTime.now())) {
            throw new OrderException(OrderError.ORDER_DRAFT_EXPIRED);
        }

        // 추가 정보 조회 및 변환
        return convertToOrderDraftResponse(orderDraft);
    }

    private OrderDraftResponse convertToOrderDraftResponse(OrderDraft orderDraft) {
        // 상품 정보 조회 및 반환
        List<OrderDraftProductResponse> products = orderDraft.products().stream()
                .map(this::convertToOrderDraftProductResponse)
                .toList();

        OrderType orderType = orderDraft.getOrderType();
        Long selectedPickupStoreId = null;
        String selectedPickupStoreName = null;
        List<PickupStoreInfo> availablePickupStores = List.of();

        if (orderType == OrderType.PICKUP) {
            // 선택된 매장 정보 (null일 수 있음 - 사용자가 아직 선택 안 함)
            selectedPickupStoreId = orderDraft.pickupDepartmentStoreId();
            availablePickupStores = products.getFirst().availableStores();

            // 선택된 매장이 있는 경우 availablePickupStores에서 매장명 찾기
            if (selectedPickupStoreId != null) {
                for (PickupStoreInfo store : availablePickupStores) {
                    if (store.storeId().equals(selectedPickupStoreId)) {
                        selectedPickupStoreName = store.storeName();
                        break;
                    }
                }
            }
        }

        return OrderDraftResponse.builder()
                .orderNumber(orderDraft.orderNumber())
                .totalAmount(orderDraft.totalAmount())
                .usedPoint(orderDraft.usedPoint())
                .donationPrice(orderDraft.donationPrice())
                .products(products)
                .deliveryAddress(orderDraft.deliveryAddress())
                .recipientName(orderDraft.recipientName())
                .selectedPickupStoreId(selectedPickupStoreId)
                .selectedPickupStoreName(selectedPickupStoreName)
                .availablePickupStores(availablePickupStores)
                .createdAt(orderDraft.createdAt())
                .validUntil(orderDraft.validUntil())
                .build();
    }

    private OrderDraftProductResponse convertToOrderDraftProductResponse(OrderProductRequest orderProductRequest) {
        // Product 테이블에서 상품 정보 조회
        Product product = productMapper.findById(orderProductRequest.productId());

        if (product == null) {
            throw new OrderException(OrderError.PRODUCT_NOT_FOUND);
        }

        // 상품의 픽업 가능한 매장 조회
        List<PickupStoreInfo> availablePickupStores = List.of();

        if (orderProductRequest.orderType() == OrderType.PICKUP) {
            availablePickupStores = departmentStoreProductMapper.findCommonPickupStoresByProductIds(
                    orderProductRequest.productId()
            );
        }

        return OrderDraftProductResponse.builder()
                .productId(orderProductRequest.productId())
                .productName(product.getName())
                .productImageUrl(product.getImageUrl())
                .price(product.getPrice())
                .quantity(orderProductRequest.quantity())
                .orderType(orderProductRequest.orderType())
                .availableStores(availablePickupStores)
                .build();
    }

    private List<PickupStoreInfo> findCommonPickupStores(List<OrderProductRequest> products) {
        OrderType orderType = products.getFirst().orderType();

        if (orderType != OrderType.PICKUP) {
            return List.of();
        }

        Set<PickupStoreInfo> commonStores = null;

        for (OrderProductRequest product : products) {
            List<PickupStoreInfo> productStores = departmentStoreProductMapper
                    .findCommonPickupStoresByProductIds(product.productId());
            Set<PickupStoreInfo> storeSet = new HashSet<>(productStores);

            if (commonStores == null) {
                commonStores = storeSet;
            } else {
                commonStores.retainAll(storeSet); // 교집합
            }

            if (commonStores.isEmpty()) {
                throw new OrderException(OrderError.DIFFERENT_PICKUP_STORES_NOT_ALLOWED);
            }
        }

        return new ArrayList<>(commonStores);
    }

    private Long calculateDefaultDonation(Long totalAmount) {
        return totalAmount % 1000;  // 1000원 미만을 기부금으로
    }

    private Long getMemberAvailablePoint(Long memberId) {
        return memberMapper.findById(memberId)
                .map(Member::getPoint)
                .orElse(0L);
    }

    private OrderDraft createOrderDraft(String orderNumber, Long memberId,
                                        List<OrderProductRequest> products, Long productAmount, Long departmentStoreId,
                                        Long usedPoint, Long donationPrice) {

        LocalDateTime now = LocalDateTime.now();

        if (departmentStoreId == null) {
            Member member = memberMapper.findById(memberId)
                    .orElseThrow(() -> new AuthException(AuthenticationError.NOT_EXIST_MEMBER_ID));

            String deliveryAddress = member.getAddress() != null ?
                    member.getAddress() : "기본 배송지 없음";

            return new OrderDraft(
                    null, orderNumber, memberId, products,
                    productAmount, usedPoint, donationPrice,
                    deliveryAddress,
                    member.getName(),
                    null, now, now.plusMinutes(30)
            );
        } else {
            return new OrderDraft(
                    null, orderNumber, memberId, products,
                    productAmount, usedPoint, donationPrice,
                    null, null,
                    departmentStoreId, now, now.plusMinutes(30)
            );
        }
    }

}
