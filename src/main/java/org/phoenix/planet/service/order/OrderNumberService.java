package org.phoenix.planet.service.order;

public interface OrderNumberService {

    String generateOrderNumber();

    boolean isValidOrderNumber(String orderNumber);

}
