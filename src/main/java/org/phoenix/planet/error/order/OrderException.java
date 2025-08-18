package org.phoenix.planet.error.order;

import lombok.Getter;
import org.phoenix.planet.constant.OrderError;

@Getter
public class OrderException extends RuntimeException {

    private final OrderError error;

    public OrderException(OrderError error) {
        super(error.getValue());
        this.error = error;
    }

    public OrderException(OrderError error, Throwable cause) {
        super(error.getValue(), cause);
        this.error = error;
    }

}
