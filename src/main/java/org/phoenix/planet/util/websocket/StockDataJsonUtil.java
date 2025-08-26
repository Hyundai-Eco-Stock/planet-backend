package org.phoenix.planet.util.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.EcoStockError;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.StockData;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;
import org.phoenix.planet.dto.eco_stock.response.ChartSingleDataResponse;
import org.phoenix.planet.error.ecoStock.EcoStockException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StockDataJsonUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private StockDataJsonUtil() {
        // util 클래스라 인스턴스화 방지
    }

    public static String serializeOhlc(OhlcDto ohlc) {
        try {
            return new ObjectMapper().writeValueAsString(ohlc);
        } catch (Exception e) {
            log.error("Failed to serialize OHLC data", e);
            return "{}";
        }
    }

    public static String serializeVolume(VolumeDto volume) {
        try {
            return new ObjectMapper().writeValueAsString(volume);
        } catch (Exception e) {
            log.error("Failed to serialize Volume data", e);
            return "{}";
        }
    }

    public static OhlcDto deserializeOhlc(Object raw) {
        try {
            return new ObjectMapper().readValue(raw.toString(), OhlcDto.class);
        } catch (Exception e) {
            log.error("Failed to deserialize OHLC data: {}", raw);
            return null;
        }
    }

    public static VolumeDto deserializeVolume(Object raw) {
        try {
            return new ObjectMapper().readValue(raw.toString(), VolumeDto.class);
        } catch (Exception e) {
            log.error("Failed to deserialize Volume data: {}", raw);
            return null;
        }
    }

    public static String serializeChartSingleDataResponse(ChartSingleDataResponse chartData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(chartData);
        } catch (Exception e) {
            log.error("차트 데이터 직렬화 실패", e);
            return "{}";
        }
    }
}
