package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.dto.receipt.PaperBagReceiptCreateRequest;
import org.phoenix.planet.issuer.EcoStockIssuer;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventConsumer {

    private final EcoStockIssuer ecoStockIssuer;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "eco.receipt-detected")
    public void onMessage(String message) throws JsonProcessingException {

        PaperBagReceiptCreateRequest event = objectMapper.readValue(message,
            PaperBagReceiptCreateRequest.class);
        log.info("Successfully deserialized event: {}", event);
        ecoStockIssuer.issueFromReceipt(event);
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {

        log.error("DLT received: {}", record);
    }
}
