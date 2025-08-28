package org.phoenix.planet.repository;

import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;
import org.phoenix.planet.util.websocket.StockDataJsonUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChartDataRedisRepository {

    private final StringRedisTemplate chartRedisTemplate;

    private static final String OHLC_KEY_PREFIX = "chart:ohlc:";
    private static final String VOLUME_KEY_PREFIX = "chart:volume:";

    // OHLC용 Lua 스크립트
    private static final String OHLC_LUA_SCRIPT = """
            -- KEYS[1]=zkey(timestamps), KEYS[2]=hkey(ohlc_data)
            -- ARGV: (timestamp, timestamp, json) 반복
            local added = 0
            for i=1, #ARGV, 3 do
              local timestamp = ARGV[i]
              local score = tonumber(ARGV[i+1])
              local json = ARGV[i+2]
              if redis.call('ZSCORE', KEYS[1], timestamp) == false then
                redis.call('ZADD', KEYS[1], score, timestamp)
                redis.call('HSET', KEYS[2], timestamp, json)
                added = added + 1
              end
            end
            return added
            """;

    // Volume용 Lua 스크립트
    private static final String VOLUME_LUA_SCRIPT = """
            -- KEYS[1]=zkey(timestamps), KEYS[2]=hkey(volume_data)
            -- ARGV: (timestamp, timestamp, json) 반복
            local added = 0
            for i=1, #ARGV, 3 do
              local timestamp = ARGV[i]
              local score = tonumber(ARGV[i+1])
              local json = ARGV[i+2]
              if redis.call('ZSCORE', KEYS[1], timestamp) == false then
                redis.call('ZADD', KEYS[1], score, timestamp)
                redis.call('HSET', KEYS[2], timestamp, json)
                added = added + 1
              end
            end
            return added
            """;

    private static final DefaultRedisScript<Long> OHLC_UPSERT_SCRIPT =
            new DefaultRedisScript<>(OHLC_LUA_SCRIPT, Long.class);

    private static final DefaultRedisScript<Long> VOLUME_UPSERT_SCRIPT =
            new DefaultRedisScript<>(VOLUME_LUA_SCRIPT, Long.class);

    public ChartDataRedisRepository(@Qualifier("webSocketRedisTemplate") StringRedisTemplate chartRedisTemplate) {
        this.chartRedisTemplate = chartRedisTemplate;
    }

    // --- OHLC 데이터 배치 저장 ---
    public void upsertOhlcBatchNoDup(Long ecoStockId, List<OhlcDto> ohlcList) {
        if (ohlcList == null || ohlcList.isEmpty()) {
            return;
        }

        List<String> keys = List.of(ohlcZKey(ecoStockId), ohlcHKey(ecoStockId));

        List<String> args = ohlcList.stream()
                .flatMap(ohlc -> toOhlcRedisArgs(ohlc).stream())
                .toList();

        Long inserted = chartRedisTemplate.execute(OHLC_UPSERT_SCRIPT, keys, args.toArray());

        log.info("Inserted {} new OHLC records into Redis (ecoStockId={})", inserted, ecoStockId);
    }

    // --- Volume 데이터 배치 저장 ---
    public void upsertVolumeBatchNoDup(Long ecoStockId, List<VolumeDto> volumeList) {
        if (volumeList == null || volumeList.isEmpty()) {
            return;
        }

        List<String> keys = List.of(volumeZKey(ecoStockId), volumeHKey(ecoStockId));

        List<String> args = volumeList.stream()
                .flatMap(volume -> toVolumeRedisArgs(volume).stream())
                .toList();

        Long inserted = chartRedisTemplate.execute(VOLUME_UPSERT_SCRIPT, keys, args.toArray());

        log.info("Inserted {} new Volume records into Redis (ecoStockId={})", inserted, ecoStockId);
    }

    // --- 단일 데이터 저장 (실시간용) ---
    public void pushSingleOhlcData(Long ecoStockId, OhlcDto ohlc) {
        List<String> keys = List.of(ohlcZKey(ecoStockId), ohlcHKey(ecoStockId));
        List<String> args = toOhlcRedisArgs(ohlc);

        chartRedisTemplate.execute(OHLC_UPSERT_SCRIPT, keys, args.toArray());
    }

    public void pushSingleVolumeData(Long ecoStockId, VolumeDto volume) {
        List<String> keys = List.of(volumeZKey(ecoStockId), volumeHKey(ecoStockId));
        List<String> args = toVolumeRedisArgs(volume);

        chartRedisTemplate.execute(VOLUME_UPSERT_SCRIPT, keys, args.toArray());
    }

    // --- 최근 데이터 조회 ---
    public OhlcDto findLastSingleToOhlc(Long ecoStockId) {
        // ZSet에서 가장 높은 score(최신 타임스탬프)를 가진 데이터 1개 조회
        Set<String> lastTimestamp = chartRedisTemplate.opsForZSet()
                .reverseRange(ohlcZKey(ecoStockId), 0, 0);

        if (lastTimestamp == null || lastTimestamp.isEmpty()) {
            return null;
        }

        String timestamp = lastTimestamp.iterator().next();
        String jsonData = (String) chartRedisTemplate.opsForHash()
                .get(ohlcHKey(ecoStockId), timestamp);

        if (jsonData == null) {
            return null;
        }

        return StockDataJsonUtil.deserializeOhlc(jsonData);
    }

    // --- 조회 ---
    public List<OhlcDto> findAllOhlcData(Long ecoStockId) {
        Set<String> timestamps = chartRedisTemplate.opsForZSet().range(ohlcZKey(ecoStockId), 0, -1);

        if (timestamps == null || timestamps.isEmpty())
            return Collections.emptyList();

        return chartRedisTemplate.opsForHash()
                .multiGet(ohlcHKey(ecoStockId), new ArrayList<>(timestamps))
                .stream()
                .filter(Objects::nonNull)
                .map(StockDataJsonUtil::deserializeOhlc)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<VolumeDto> findAllVolumeData(Long ecoStockId) {
        Set<String> timestamps = chartRedisTemplate.opsForZSet().range(volumeZKey(ecoStockId), 0, -1);

        if (timestamps == null || timestamps.isEmpty())
            return Collections.emptyList();

        return chartRedisTemplate.opsForHash()
                .multiGet(volumeHKey(ecoStockId), new ArrayList<>(timestamps))
                .stream()
                .filter(Objects::nonNull)
                .map(StockDataJsonUtil::deserializeVolume)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    // --- 존재 체크 메서드들 ---
    public boolean existOhlcDto(Long ecoStockId) {
        return chartRedisTemplate.hasKey(ohlcZKey(ecoStockId)) &&
                chartRedisTemplate.hasKey(ohlcHKey(ecoStockId));
    }

    public boolean existVolumeDto(Long ecoStockId) {
        return chartRedisTemplate.hasKey(volumeZKey(ecoStockId)) &&
                chartRedisTemplate.hasKey(volumeHKey(ecoStockId));
    }

    // --- 타임스탬프 체크 메서드 ---

    /**
     * 현재 OHLC 데이터의 타임스탬프가 Redis에 저장된 최신 데이터와 1분 이상 차이나는지 체크
     *
     * @param currentOhlc 현재 OHLC 데이터
     * @return true: 1분 이상 차이남 (재초기화 필요), false: 정상적인 1분 간격
     */
    public boolean checkTimestamp(Long ecoStockId, OhlcDto currentOhlc) {
        OhlcDto lastOhlc = findLastSingleToOhlc(ecoStockId);

        if (lastOhlc == null) {
            log.info("이전 OHLC 데이터 없음, 재초기화 필요 (ecoStockId={})", ecoStockId);
            return true;
        }

        long currentTimestamp = currentOhlc.time();
        long lastTimestamp = lastOhlc.time();
        long timeDifference = currentTimestamp - lastTimestamp;

        // 🔥 초 단위로 수정!
        long oneMinute = 60L;        // 60초 = 1분
        long allowedError = 10L;     // 10초 허용 오차

        // 시간 차이가 1분±10초 범위를 벗어나면 재초기화 필요
        boolean isNormalInterval = (timeDifference == 0) ||
                (Math.abs(timeDifference - oneMinute) <= allowedError);

        return !isNormalInterval; // 정상이 아니면 재초기화
    }

    // --- 내부 Helper 메서드들 ---
    private String ohlcZKey(Long ecoStockId) {
        return OHLC_KEY_PREFIX + ecoStockId + ":timestamps";
    }

    private String ohlcHKey(Long ecoStockId) {
        return OHLC_KEY_PREFIX + ecoStockId + ":data";
    }

    private String volumeZKey(Long ecoStockId) {
        return VOLUME_KEY_PREFIX + ecoStockId + ":timestamps";
    }

    private String volumeHKey(Long ecoStockId) {
        return VOLUME_KEY_PREFIX + ecoStockId + ":data";
    }

    private List<String> toOhlcRedisArgs(OhlcDto ohlc) {
        String timestampStr = String.valueOf(ohlc.time());
        return List.of(
                timestampStr,                    // timestamp (key)
                timestampStr,                    // timestamp (score)
                StockDataJsonUtil.serializeOhlc(ohlc)             // JSON data
        );
    }

    private List<String> toVolumeRedisArgs(VolumeDto volume) {
        String timestampStr = String.valueOf(volume.time());
        return List.of(
                timestampStr,                    // timestamp (key)
                timestampStr,                    // timestamp (score)
                StockDataJsonUtil.serializeVolume(volume)         // JSON data
        );
    }
}