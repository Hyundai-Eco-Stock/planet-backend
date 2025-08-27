package org.phoenix.planet.error.payment;

import lombok.Getter;
import org.phoenix.planet.constant.PaymentError;

@Getter
public class PaymentException extends RuntimeException {

    private final PaymentError error;

    public PaymentException(PaymentError error) {
        super(error.getValue());
        this.error = error;
    }

    public PaymentException(PaymentError error, Throwable cause) {
        super(error.getValue(), cause);
        this.error = error;
    }

}
