package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.dto.receipt.PaperBagReceiptCreateRequest;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventConsumer {

    private final EcoStockIssueService ecoStockIssueService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "eco.paper-bag-no-use-receipt-detected")
    public void onMessage(String message) throws JsonProcessingException {

        PaperBagReceiptCreateRequest event = objectMapper.readValue(message,
            PaperBagReceiptCreateRequest.class);
        log.info("Successfully deserialized event: {}", event);
        ecoStockIssueService.publish(event.memberId(), 4L, 1);
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {

        log.error("DLT received: {}", record);
    }
}
