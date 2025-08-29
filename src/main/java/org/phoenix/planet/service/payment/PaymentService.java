package org.phoenix.planet.service.payment;

import org.phoenix.planet.dto.payment.request.PaymentCancelReqeust;
import org.phoenix.planet.dto.payment.request.PaymentConfirmRequest;
import org.phoenix.planet.dto.payment.response.PaymentCancelResponse;
import org.phoenix.planet.dto.payment.response.PaymentConfirmResponse;

public interface PaymentService {

    PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request, Long memberId);

    PaymentCancelResponse cancelEntireOrder(Long orderHistoryId, PaymentCancelReqeust request);

}
