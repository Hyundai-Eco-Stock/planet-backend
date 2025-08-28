package org.phoenix.planet.service.order;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.repository.OrderNumberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderNumberServiceImpl implements OrderNumberService {

    private final OrderNumberRepository orderNumberRepository;

    private static final String ORDER_PREFIX = "ORD";

    @Override
    public String generateOrderNumber() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long sequence = orderNumberRepository.getNextSequence(today);
        String formattedSequence = String.format("%06d", sequence);

        return ORDER_PREFIX + today + formattedSequence;
    }

    @Override
    public boolean isValidOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.length() != 17) {
            return false;
        }

        return orderNumber.startsWith(ORDER_PREFIX) &&
                orderNumber.substring(3, 11).matches("\\d{8}") &&
                orderNumber.substring(11).matches("\\d{6}");
    }

}
