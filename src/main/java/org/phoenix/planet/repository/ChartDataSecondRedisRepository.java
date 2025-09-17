package org.phoenix.planet.repository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.EcoStockError;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockWithLastPrice;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;
import org.phoenix.planet.dto.eco_stock.response.ChartSingleDataResponse;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;
import org.phoenix.planet.dto.eco_stock_info.response.EcoStockPriceResponse;
import org.phoenix.planet.error.ecoStock.EcoStockException;
import org.phoenix.planet.mapper.EcoStockMapper;
import org.phoenix.planet.util.ecoStock.StockChartUtil;
import org.phoenix.planet.util.websocket.StockDataJsonUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChartDataSecondRedisRepository {

    private final StringRedisTemplate chartRedisTemplate;

    private static final String OHLC_KEY_PREFIX = "chart:second:ohlc:";
    private static final String VOLUME_KEY_PREFIX = "chart:second:volume:";
    private static final String STOCK_PRICE_KEY = "stock:price:";
    private static final String CACHING_OHLC_KEY_PREFIX = "chart:ohlc:";
    private static final String CACHING_VOLUME_KEY_PREFIX = "chart:volume:";

    private static final String UNIFIED_UPDATE_SCRIPT = """
            local stock_price_key = KEYS[1]     -- stock:price:123
            local ohlc_key = KEYS[2]            -- chart:second:ohlc:123:2025-09-08
            local volume_key = KEYS[3]          -- chart:second:volume:123:2025-09-08
            local minute_field = KEYS[4]        -- "14:30"
            
            local trade_quantity = tonumber(ARGV[1])  -- 양수: 매도, 음수: 매수
            local ttl_seconds = tonumber(ARGV[2])
            local real_epoch_time = tonumber(ARGV[3])  -- Java에서 전달받은 시간
            local epoch_time = tonumber(ARGV[4])       -- Java에서 전달받은 분 단위 시간
            
            -- 매도/매수 구분
            local is_sell = trade_quantity > 0
            local abs_quantity = math.abs(trade_quantity)
            
            -- 1. 주식 가격 업데이트
            local current_price = redis.call('HGET', stock_price_key, 'price')
            local current_quantity = redis.call('HGET', stock_price_key, 'quantity')
            
            if not current_price or not current_quantity then
                return {-1, 'STOCK_NOT_FOUND'}
            end
            
            local price = tonumber(current_price)
            local quantity = tonumber(current_quantity)
            
            -- 매도시에만 수량 부족 체크
            if is_sell and quantity < abs_quantity then
                return {-2, 'INSUFFICIENT_QUANTITY'}
            end
            
            -- 수량 계산
            local new_quantity
            if is_sell then
                new_quantity = quantity - abs_quantity  -- 소각
                if new_quantity <= 0 then
                    return {-3, 'CANNOT_BURN_ALL_STOCKS'}
                end
            else
                new_quantity = quantity + abs_quantity  -- 발행
            end
            
            -- 가격 계산 (공급량 변화에 따른 기본값을 2배로 강화)
            local supply_change_ratio = quantity / new_quantity
            
            local price_multiplier = 1 + ((supply_change_ratio - 1) * 3)
            
            local base_new_price = price * price_multiplier
            
            local new_price = base_new_price
            
            -- 주식 가격 원자적 업데이트
            redis.call('HSET', stock_price_key, 'price', new_price)
            redis.call('HSET', stock_price_key, 'quantity', new_quantity)
            redis.call('HSET', stock_price_key, 'last_updated', real_epoch_time)
            
            -- 가격 히스토리 저장 (DB 동기화용)
            local stock_id = string.sub(KEYS[1], 13)  -- "stock:price:123"에서 "123" 추출
            local price_history_key = "price_history:" .. stock_id
            local trade_type = is_sell and "SELL" or "BUY"
            local history_data = string.format('{"stock_price_history_id":%d,"time":%d,"price":%f,"old_price":%f,"quantity":%d,"trade_quantity":%d,"trade_type":"%s"}',
                0, real_epoch_time, new_price, price, new_quantity, abs_quantity, trade_type)
            
            redis.call('LPUSH', price_history_key, history_data)
            redis.call('LTRIM', price_history_key, 0, 999)
            redis.call('EXPIRE', price_history_key, ttl_seconds)
            
            -- 2. 차트 데이터 업데이트 (체결가는 이전 가격)
            local old_ohlc_json = redis.call('HGET', ohlc_key, minute_field)
            local old_volume_json = redis.call('HGET', volume_key, minute_field)
            
            -- OHLC 계산 (체결가 = price, 새 시장가 = new_price)
            local new_ohlc_json
            if old_ohlc_json then
                local open = tonumber(old_ohlc_json:match('"open":([%d%.]+)'))
                local high = tonumber(old_ohlc_json:match('"high":([%d%.]+)'))
                local low = tonumber(old_ohlc_json:match('"low":([%d%.]+)'))
            
                high = math.max(high, new_price) -- 체결가 반영
                low = (low == 0) and new_price or math.min(low, new_price)
            
                new_ohlc_json = string.format('{"stockPriceHistoryId":%d,"time":%d,"open":%f,"high":%f,"low":%f,"close":%f,"isEmpty":false}',
                    0, epoch_time, open, high, low, new_price)
            else
                -- 🟢 최신 데이터 찾기 (ZSET에서 직전 분 timestamp 가져오기)
                local ohlc_z_key = "chart:ohlc:" .. stock_id .. ":timestamps"
                local ohlc_h_key = "chart:ohlc:" .. stock_id .. ":data"
            
                local last_ts_result = redis.call('ZREVRANGE', ohlc_z_key, 0, 0)
                local open_price = new_price
                
                if last_ts_result and #last_ts_result > 0 then  -- ✅ 올바른 변수명
                    local last_ts = last_ts_result[1]
                    local prev_json = redis.call('HGET', ohlc_h_key, last_ts)
                    if prev_json then
                        local prev_close = tonumber(prev_json:match('"close":([%d%.]+)'))
                        if prev_close then\s
                            open_price = prev_close\s
                        end
                    end
                end
            
                new_ohlc_json = string.format(
                    '{"stockPriceHistoryId":%d,"time":%d,"open":%f,"high":%f,"low":%f,"close":%f,"isEmpty":false}',
                    0, epoch_time, open_price, price, price, price
                )
            end
            
            -- Volume 계산
            local new_volume_json
            if old_volume_json then
                local old_value = tonumber(old_volume_json:match('"value":(%d+)'))
                local old_buy_count = tonumber(old_volume_json:match('"buyCount":(%d+)'))
                local old_sell_count = tonumber(old_volume_json:match('"sellCount":(%d+)'))
            
                local new_value = old_value + abs_quantity
                local new_buy_count = old_buy_count
                local new_sell_count = old_sell_count
            
                if is_sell then
                    new_sell_count = old_sell_count + abs_quantity
                else
                    new_buy_count = old_buy_count + abs_quantity
                end
            
                local color = (new_sell_count > new_buy_count) and "SELL" or "BUY"
            
                new_volume_json = string.format('{"stockPriceHistoryId":%d,"time":%d,"value":%d,"color":"%s","buyCount":%d,"sellCount":%d}',
                    0, epoch_time, new_value, color, new_buy_count, new_sell_count)
            else
                -- 첫 거래
                if is_sell then
                    new_volume_json = string.format('{"stockPriceHistoryId":%d,"time":%d,"value":%d,"color":"SELL","buyCount":0,"sellCount":%d}',
                        0, epoch_time, abs_quantity, abs_quantity)
                else
                    new_volume_json = string.format('{"stockPriceHistoryId":%d,"time":%d,"value":%d,"color":"BUY","buyCount":%d,"sellCount":0}',
                        0, epoch_time, abs_quantity, abs_quantity)
                end
            end
            
            -- 차트 데이터 저장
            redis.call('HSET', ohlc_key, minute_field, new_ohlc_json)
            redis.call('HSET', volume_key, minute_field, new_volume_json)
            redis.call('EXPIRE', ohlc_key, ttl_seconds)
            redis.call('EXPIRE', volume_key, ttl_seconds)
            
            return { 1,
                     string.format("%.2f", price),
                     string.format("%.2f", new_price),
                     new_quantity,
                     new_ohlc_json,
                     new_volume_json,
                     0,
                     real_epoch_time
                     }
            """;

    private static final String GET_AND_PUSH_CHART_DATA_SCRIPT = """
        local ohlc_key_read = KEYS[1]      -- 초 단위 OHLC 읽기용
        local volume_key_read = KEYS[2]    -- 초 단위 Volume 읽기용
        local minute_field_read = KEYS[3]  -- 초 단위 필드
        
        local ohlc_z_key = KEYS[4]         -- 분봉 OHLC ZSET
        local ohlc_h_key = KEYS[5]         -- 분봉 OHLC HASH
        local volume_z_key = KEYS[6]       -- 분봉 Volume ZSET
        local volume_h_key = KEYS[7]       -- 분봉 Volume HASH
        local minute_field_write = KEYS[8] -- 분 단위 타임스탬프(epoch)
        
        local ttl_seconds = tonumber(ARGV[1])
        
        -- 1. 차트 데이터 조회
        local ohlc_json = redis.call('HGET', ohlc_key_read, minute_field_read)
        local volume_json = redis.call('HGET', volume_key_read, minute_field_read)
        
        -- 2. 분봉 데이터 저장 (Z/H 방식)
        if ohlc_json then
            redis.call('ZADD', ohlc_z_key, minute_field_write, minute_field_write)
            redis.call('HSET', ohlc_h_key, minute_field_write, ohlc_json)
            redis.call('EXPIRE', ohlc_z_key, ttl_seconds)
            redis.call('EXPIRE', ohlc_h_key, ttl_seconds)
        end
        
        if volume_json then
            redis.call('ZADD', volume_z_key, minute_field_write, minute_field_write)
            redis.call('HSET', volume_h_key, minute_field_write, volume_json)
            redis.call('EXPIRE', volume_z_key, ttl_seconds)
            redis.call('EXPIRE', volume_h_key, ttl_seconds)
        end
        
        return {ohlc_json, volume_json}
        
        """;
    private final EcoStockMapper ecoStockMapper;


    public ChartDataSecondRedisRepository(
        @Qualifier("webSocketRedisTemplate") StringRedisTemplate chartRedisTemplate,
        EcoStockMapper ecoStockMapper) {

        this.chartRedisTemplate = chartRedisTemplate;
        this.ecoStockMapper = ecoStockMapper;
    }

    public OhlcDto SecondOhlcDtoData(Long stockId, LocalDateTime now) {

        String key = buildKey(OHLC_KEY_PREFIX, stockId, now);
        String field = buildField(now);

        String json = (String) chartRedisTemplate.opsForHash().get(key, field);

        if (json == null) {
            return null;
        }

        return StockDataJsonUtil.deserializeOhlc(json);
    }

    public VolumeDto SecondVolumeDtoData(Long stockId, LocalDateTime now) {

        String key = buildKey(VOLUME_KEY_PREFIX, stockId, now);
        String field = buildField(now);

        String json = (String) chartRedisTemplate.opsForHash().get(key, field);
        if (json == null) {
            return null;
        }
        return StockDataJsonUtil.deserializeVolume(json);
    }

    public UnifiedUpdateResult processTradeWithChart(Long stockId, Integer tradeQuantity,
        LocalDateTime time) {

        ensureStockPriceInitialized(stockId, time); // ✅ 없으면 만든다

        long epochTime = StockChartUtil.convertLocalDateTimeToEpoch(time);

        long truncatedEpochTime = epochTime - (epochTime % 60); // 분 단위로 truncate

        List<String> keys = getKeys(stockId, time);

        List<String> args = Arrays.asList(
            tradeQuantity.toString(),
            String.valueOf(Duration.ofDays(90).getSeconds()),
            String.valueOf(epochTime),           // real_epoch_time
            String.valueOf(truncatedEpochTime)   // epoch_time (분 단위)
        );

        @SuppressWarnings("unchecked")
        List<Object> results = (List<Object>) chartRedisTemplate.execute(
            RedisScript.of(UNIFIED_UPDATE_SCRIPT, List.class), keys, args.toArray());

        Long status = (Long) results.get(0);

        if (status == 1) {

            return parseResults(results, tradeQuantity);
        } else {
            log.error(results.get(1).toString());
            throw new EcoStockException(EcoStockError.STOCK_REDIS_SERVER_ERROR);
        }
    }

    private void ensureStockPriceInitialized(Long stockId, LocalDateTime now) {
        String key = "stock:price:" + stockId;

        List<Object> vals = chartRedisTemplate.opsForHash()
                .multiGet(key, Arrays.asList("price", "quantity"));

        boolean missing = (vals == null || vals.size() < 2 || vals.get(0) == null || vals.get(1) == null);

        if (!missing) return;

        EcoStockWithLastPrice ecoStockWithLastPrice = ecoStockMapper.findAllWithLastPriceByStockId(stockId);

        initializeStockPrice(stockId, ecoStockWithLastPrice.getLastPrice(),
            ecoStockWithLastPrice.getQuantity(), ecoStockWithLastPrice.getStockTime());
    }

    private List<String> getKeys(Long stockId, LocalDateTime time) {

        String stockPriceKey = STOCK_PRICE_KEY + stockId;

        String ohlcKey = buildKey(OHLC_KEY_PREFIX, stockId, time);

        String volumeKey = buildKey(VOLUME_KEY_PREFIX, stockId, time);

        String minuteField = buildField(time);

        return Arrays.asList(stockPriceKey, ohlcKey, volumeKey, minuteField);
    }

    private UnifiedUpdateResult parseResults(List<Object> results, int tradeQuantity) {

        BigDecimal executedPrice = new BigDecimal(results.get(1).toString());
        BigDecimal newMarketPrice = new BigDecimal(results.get(2).toString());

        Integer newQuantity = ((Long) results.get(3)).intValue();

        String ohlcJson = results.get(4).toString();

        String volumeJson = results.get(5).toString();

        // 결과에서 시간도 받기
        Long historyId = (Long) results.get(6);

        Long transactionTimestamp = (Long) results.get(7);

        LocalDateTime transactionTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(transactionTimestamp),
            ZoneId.systemDefault()
        );

        // JSON을 DTO로 변환
        OhlcDto ohlcDto = StockDataJsonUtil.deserializeOhlc(ohlcJson);

        VolumeDto volumeDto = StockDataJsonUtil.deserializeVolume(volumeJson);

        return UnifiedUpdateResult.builder()
            .executedPrice(executedPrice.doubleValue())
            .newMarketPrice(newMarketPrice.doubleValue())
            .newQuantity(newQuantity)
            .ohlcDto(ohlcDto)
            .volumeDto(volumeDto)
            .historyId(historyId)
            .transactionTime(transactionTime)
            .build();
    }


    // 초기 주식 가격 설정
    public void initializeStockPrice(Long stockId, Double initialPrice, Long initialQuantity,LocalDateTime stockTime) {

        String key = STOCK_PRICE_KEY + stockId;
        log.info("initialize stock price " + initialPrice + " " + initialQuantity);
        Map<String, String> stockData = Map.of(
            "price", initialPrice.toString(),
            "quantity", initialQuantity.toString(),
            "last_updated", String.valueOf(System.currentTimeMillis())
        );

        stockData.forEach((field, value) -> {
            chartRedisTemplate.opsForHash().putIfAbsent(key, field, value);
        });
    }

    private String buildKey(String prefix, Long stockId, LocalDateTime now) {
        // 날짜별로 분리 → ex) chart:second:ohlc:123:2025-09-02
        return prefix + stockId + ":" + now.toLocalDate();
    }

    private String buildField(LocalDateTime now) {
        // 분:초 단위 필드 → ex) 20:05
        return String.format("%02d:%02d", now.getHour(), now.getMinute());
    }

    public ChartSingleDataResponse getAndPushMinuteChartData(Long stockId,
        LocalDateTime targetTime) {
        // 읽기 키들 (초 단위)
        String ohlcKeyRead = buildKey(OHLC_KEY_PREFIX, stockId, targetTime);
        String volumeKeyRead = buildKey(VOLUME_KEY_PREFIX, stockId, targetTime);
        String minuteFieldRead = buildField(targetTime);

        // 쓰기 키들 (분봉, Z/H 구조)
        String ohlcZKey = ohlcZKey(stockId);
        String ohlcHKey = ohlcHKey(stockId);
        String volumeZKey = volumeZKey(stockId);
        String volumeHKey = volumeHKey(stockId);

        String minuteFieldWrite = String.valueOf(
            StockChartUtil.convertLocalDateTimeToEpoch(targetTime.truncatedTo(ChronoUnit.MINUTES))
        );

        List<String> keys = Arrays.asList(
            ohlcKeyRead, volumeKeyRead, minuteFieldRead,
            ohlcZKey, ohlcHKey, volumeZKey, volumeHKey,
            minuteFieldWrite
        );

        List<String> args = List.of(String.valueOf(Duration.ofMinutes(300).getSeconds()));

        @SuppressWarnings("unchecked")
        List<Object> results = (List<Object>) chartRedisTemplate.execute(
            RedisScript.of(GET_AND_PUSH_CHART_DATA_SCRIPT, List.class), keys, args.toArray());

        String ohlcJson = (String) results.get(0);
        String volumeJson = (String) results.get(1);

        log.info("getAndPushMinuteChartData " + ohlcJson + " " + volumeJson);
        OhlcDto ohlcDto = (ohlcJson != null) ? StockDataJsonUtil.deserializeOhlc(ohlcJson) : null;
        VolumeDto volumeDto =
            (volumeJson != null) ? StockDataJsonUtil.deserializeVolume(volumeJson) : null;

        return new ChartSingleDataResponse(stockId, ohlcDto, volumeDto);
    }


    // ChartDataSecondRedisRepository에 추가
    public List<String> getPriceHistoryData(Long stockId) {

        String priceHistoryKey = "price_history:" + stockId;
        return chartRedisTemplate.opsForList().range(priceHistoryKey, 0, -1);
    }

    public void clearPriceHistoryData(Long stockId) {

        String priceHistoryKey = "price_history:" + stockId;
        chartRedisTemplate.delete(priceHistoryKey);
    }

    // 가장 단순 / 직관 버전: KEYS + entries 반복
    public List<EcoStockPriceResponse> getAllCurrentStockPricesBruteForce() {
        // 1) 모든 키 찾기
        Set<String> keys = chartRedisTemplate.keys("stock:price:*");
        if (keys == null || keys.isEmpty()) return List.of();

        ZoneId zone = ZoneId.systemDefault();

        // 2) 각 키의 해시를 읽어 DTO로 매핑
        return keys.stream()
                .sorted() // 정렬 원하면 유지, 필요 없으면 제거
                .map(key -> {
                    Map<Object, Object> map = chartRedisTemplate.opsForHash().entries(key);
                    if (map == null || map.isEmpty()) return null;

                    String priceStr   = (String) map.get("price");
                    String qtyStr     = (String) map.get("quantity");
                    String updatedStr = (String) map.get("last_updated");

                    Double price = priceStr != null ? Double.valueOf(priceStr) : 0.0;
                    Long quantity = qtyStr != null ? Long.valueOf(qtyStr) : 0L;

                    LocalDateTime stockTime = null;
                    if (updatedStr != null) {
                        long ms = Long.parseLong(updatedStr);
                        stockTime = Instant.ofEpochMilli(ms).atZone(zone).toLocalDateTime();
                    }

                    // 키에서 ID가 필요하면 파싱, 필요 없으면 null로 두거나 필드 제거
                    Long ecoStockId = parseIdFromKey(key); // "stock:price:123" -> 123

                    return EcoStockPriceResponse.builder()
                            .ecoStockId(ecoStockId)
                            .stockPrice(price)
                            .stockTime(stockTime)
                            // .quantity(quantity)   // DTO에 있으면 추가
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private static Long parseIdFromKey(String key) {
        int idx = key.lastIndexOf(':');
        if (idx < 0 || idx == key.length() - 1) return null;
        try { return Long.valueOf(key.substring(idx + 1)); }
        catch (NumberFormatException e) { return null; }
    }

    // --- 내부 Helper 메서드들 ---
    private String ohlcZKey(Long ecoStockId) {

        return "chart:ohlc:" + ecoStockId + ":timestamps";
    }

    private String ohlcHKey(Long ecoStockId) {

        return CACHING_OHLC_KEY_PREFIX + ecoStockId + ":data";
    }

    private String volumeZKey(Long ecoStockId) {

        return "chart:volume:" + ecoStockId + ":timestamps";
    }

    private String volumeHKey(Long ecoStockId) {

        return CACHING_VOLUME_KEY_PREFIX + ecoStockId + ":data";
    }

}