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
import org.phoenix.planet.error.auth.AuthException;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.mapper.DepartmentStoreProductMapper;
import org.phoenix.planet.mapper.MemberMapper;
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
    private final MemberMapper memberMapper;

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
