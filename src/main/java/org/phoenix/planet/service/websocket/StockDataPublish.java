package org.phoenix.planet.service.websocket;

import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.response.ChartSingleDataResponse;
import org.phoenix.planet.util.websocket.StockDataJsonUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockDataPublish {
    private static final String CHANNEL = "stock-channel";
    private final StringRedisTemplate webSocketRedisTemplate;

    public StockDataPublish(
            @Qualifier("webSocketRedisTemplate") StringRedisTemplate webSocketRedisTemplate) {
        this.webSocketRedisTemplate = webSocketRedisTemplate;
    }

    public void pushData(ChartSingleDataResponse chartSingleDataResponse) {

        // JSON 문자열로 변환 후 Redis 채널에 발행
        String jsonData = StockDataJsonUtil.serializeChartSingleDataResponse(chartSingleDataResponse);

        webSocketRedisTemplate.convertAndSend(CHANNEL, jsonData);

        log.trace("{}", chartSingleDataResponse);
    }
}
