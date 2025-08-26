package org.phoenix.planet.error.ecoStock;

import lombok.Getter;
import org.phoenix.planet.constant.EcoStockError;

@Getter
public class EcoStockException extends RuntimeException {
    private final EcoStockError error;

    public EcoStockException(EcoStockError error) {
        super(error.getValue());
        this.error = error;
    }

    public EcoStockException(EcoStockError error, Throwable cause) {
        super(error.getValue(), cause); // cause도 같이 전달
        this.error = error;
    }
}
