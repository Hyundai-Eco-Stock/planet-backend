package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.constant.PaymentStatus;
import org.phoenix.planet.dto.payment.raw.PaymentHistory;

@Mapper
public interface PaymentHistoryMapper {

    void insert(PaymentHistory paymentHistory);

    PaymentHistory findByPaymentKey(@Param("paymentKey") String paymentKey);

    PaymentHistory findByOrderHistoryId(@Param("orderHistoryId") Long orderHistoryId);

    int updatePaymentStatus(@Param("paymentId") Long paymentId, @Param("paymentStatus") PaymentStatus paymentStatus);

    int updatePaymentStatusWithBalance(
        @Param("paymentId") Long paymentId,
        @Param("paymentStatus") PaymentStatus paymentStatus,
        @Param("balanceAmount") Long balanceAmount
    );

}
