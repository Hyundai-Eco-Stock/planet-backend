package org.phoenix.planet.constant.error;

public enum SellStockErrorCode {
    STOCK_PRICE_HISTORY_NOT_FOUND(-1, EcoStockError.STOCK_PRICE_HISTORY_NOT_FOUND),
    PRICE_MISMATCH(-2, EcoStockError.PRICE_MISMATCH),
    MEMBER_STOCK_INFO_NOT_FOUND(-3, EcoStockError.MEMBER_STOCK_INFO_NOT_FOUND),
    INSUFFICIENT_STOCK(-4, EcoStockError.INSUFFICIENT_STOCK);

    private final int errorCode;
    private final EcoStockError ecoStockError;

    SellStockErrorCode(int errorCode, EcoStockError ecoStockError) {

        this.errorCode = errorCode;
        this.ecoStockError = ecoStockError;
    }

    public static EcoStockError getEcoStockError(int errorCode) {

        for (SellStockErrorCode sellErrorCode : values()) {
            if (sellErrorCode.errorCode == errorCode) {
                return sellErrorCode.ecoStockError;
            }
        }
        // 기본값 또는 알 수 없는 에러 처리
        return EcoStockError.INTERNAL_SERVER_ERROR; // 또는 적절한 기본 에러
    }
}