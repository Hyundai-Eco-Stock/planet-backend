package org.phoenix.planet.service.payment;

public interface QrCodeService {

    String generatePickupQRCode(Long orderHistoryId);

    boolean validateQRCode(String qrCodeData);

}
