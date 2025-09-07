package org.phoenix.planet.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.error.EcoStockError;
import org.phoenix.planet.dto.eco_stock.response.ChartSingleDataResponse;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockDataSubscriber implements MessageListener {

    private final SimpMessageSendingOperations sendingOperations;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            String jsonData = new String(message.getBody());
            log.trace("📨 Redis에서 메시지 수신: {}", jsonData);

            ChartSingleDataResponse chartData = objectMapper.readValue(jsonData,
                ChartSingleDataResponse.class);
            Long stockId = chartData.ecoStockId();

            String topic = "/topic/stock" + stockId + "/update";
            sendingOperations.convertAndSend(topic, chartData);

            log.trace("📊 웹소켓 브로드캐스트: {} → {}", topic, chartData);

        } catch (JsonProcessingException e) {
            log.error("{}: {}", EcoStockError.JSON_DESERIALIZATION_FAILED.getValue(),
                e.getMessage());
        } catch (Exception e) {
            log.error("{}: {}", EcoStockError.STOCK_DATA_PROCESSING_FAILED.getValue(),
                e.getMessage());
        }
    }
}