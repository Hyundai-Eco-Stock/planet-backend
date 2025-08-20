package org.phoenix.planet.service.order;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.constant.AuthenticationError;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.constant.OrderType;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.dto.order.raw.OrderDraft;
import org.phoenix.planet.dto.order.raw.OrderValidationResult;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.request.OrderProductRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.error.auth.AuthException;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.mapper.DepartmentStoreProductMapper;
import org.phoenix.planet.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderValidationService orderValidationService;
    private final OrderDraftService orderDraftService;
    private final OrderNumberService orderNumberService;
    private final DepartmentStoreProductMapper departmentStoreProductMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, Long memberId) {
        // ecoDeal인 경우 매장 자동 매핑
        Long departmentStoreId = determineDepartmentStoreId(request.products());

        // 상품 검증
        OrderValidationResult validationResult = orderValidationService
                .validateAndCalculate(request.products(), departmentStoreId);
        String orderNumber = orderNumberService.generateOrderNumber();

        // 사용자 보유 포인트 조회
        Long availablePoint = getMemberAvailablePoint(memberId);

        // 기본 기부금 계산
        Long defaultDonationPrice = calculateDefaultDonation(validationResult.totalAmount());

        OrderDraft orderDraft = createOrderDraft(
                orderNumber,
                memberId,
                request.products(),
                validationResult.totalAmount(),  // 순수 상품 금액만
                departmentStoreId,
                availablePoint,
                defaultDonationPrice
        );
        orderDraftService.saveOrderDraft(orderDraft);

        return new CreateOrderResponse(
                orderNumber,
                validationResult.totalAmount(), // 상품 금액만 반환
                "주문서가 성공적으로 생성되었습니다."
        );
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

    private Long determineDepartmentStoreId(List<OrderProductRequest> products) {
        OrderType orderType = products.getFirst().orderType();

        if (orderType == OrderType.PICKUP) {
            // 모든 ecoDeal 상품이 동일한 매장에서 판매되는지 확인
            Set<Long> departmentStoreIds = products.stream()
                    .map(product -> departmentStoreProductMapper.findDepartmentStoreIdByProductId(product.productId())
                            .orElseThrow(() -> new OrderException(OrderError.PICKUP_STORE_NOT_FOUND)))
                    .collect(Collectors.toSet());

            if (departmentStoreIds.size() > 1) {
                throw new OrderException(OrderError.DIFFERENT_PICKUP_STORES_NOT_ALLOWED);
            }

            return departmentStoreIds.iterator().next();
        }

        return null; // 배송 주문인 경우
    }

}
