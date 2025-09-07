package org.phoenix.planet.error.raffle;

import lombok.Getter;
import org.phoenix.planet.constant.error.RaffleError;

@Getter
public class RaffleException extends RuntimeException {

    private final RaffleError error;

    public RaffleException(RaffleError error) {

        super(error.getValue());
        this.error = error;
    }

    public RaffleException(RaffleError error, Throwable cause) {

        super(error.getValue(), cause);
        this.error = error;
    }
}
