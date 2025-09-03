package org.phoenix.planet.util.ecoStock;

import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.StockData;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 주식 차트 데이터 변환 유틸리티 클래스
 */
public class StockChartUtil {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final int KST_OFFSET = 9 * 60 * 60;

    /**
     * 빈 시간을 1분 간격으로 채워서 연속적인 시간축을 만듭니다
     *
     * @param sortedList 시간순으로 정렬된 StockData 리스트
     * @return 빈 시간이 채워진 StockData 리스트
     */
    public static List<StockData> fillMissingMinutes(List<StockData> sortedList) {
        if (sortedList == null || sortedList.isEmpty()) {
            return new ArrayList<>();
        }

        List<StockData> result = new ArrayList<>();
        AtomicLong fakeIdGenerator = new AtomicLong(-1L); // -1부터 시작해서 계속 감소

        for (int i = 0; i < sortedList.size(); i++) {
            StockData current = sortedList.get(i);
            result.add(current);

            if (i < sortedList.size() - 1) {
                StockData next = sortedList.get(i + 1);

                LocalDateTime currentTime = current.getStockTime();
                LocalDateTime nextTime = next.getStockTime();

                while (currentTime.plusMinutes(1).isBefore(nextTime)) {
                    currentTime = currentTime.plusMinutes(1);
                    result.add(createFakeStockData(fakeIdGenerator, current, currentTime));
                }
            }
        }
        return result;
    }

    /**
     * StockData 리스트를 OHLC 데이터로 변환합니다
     *
     * @param data StockData 리스트
     * @return OHLC 데이터 리스트
     */
    public static List<OhlcDto> convertToOhlc(List<StockData> data) {
        if (data == null || data.size() < 2) {
            return new ArrayList<>();
        }

        List<OhlcDto> result = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            StockData prev = data.get(i - 1);
            StockData current = data.get(i);
            result.add(createOhlcDto(prev.getStockPrice(), current));
        }
        return result;
    }

    /**
     * StockData 리스트를 Volume 데이터로 변환합니다
     *
     * @param data StockData 리스트
     * @return Volume 데이터 리스트
     */
    public static List<VolumeDto> convertToVolume(List<StockData> data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        List<VolumeDto> result = new ArrayList<>();
        for (StockData stockData : data) {
            long timestamp = stockData.getStockTime()
                    .atZone(KOREA_ZONE)
                    .toEpochSecond() + KST_OFFSET;

            int total = stockData.getBuyCount() + stockData.getSellCount();

            String color = determineVolumeColor(total, stockData.getBuyCount(), stockData.getSellCount());

            result.add(new VolumeDto(stockData.getStockPriceHistoryId(), timestamp, total, color, stockData.getSellCount(),stockData.getBuyCount()));
        }
        return result;
    }

    /**
     * 단일 StockData를 OHLC로 변환합니다 (실시간 업데이트용)
     *
     * @param current        현재 데이터
     * @param prevStockPrice 이전 데이터
     * @return OHLC 데이터 (null 가능)
     */
    public static OhlcDto convertSingleToOhlc(StockData current, long prevStockPrice) {
        if (current == null) {
            return null;
        }
        return createOhlcDto(prevStockPrice, current);
    }

    /**
     * 단일 StockData를 Volume으로 변환합니다 (실시간 업데이트용)
     *
     * @param stockData 변환할 데이터
     * @return Volume 데이터 (null 가능)
     */
    public static VolumeDto convertSingleToVolume(StockData stockData) {
        if (stockData == null) {
            return null;
        }

        long timestamp = stockData.getStockTime()
                .atZone(KOREA_ZONE)
                .toEpochSecond() + KST_OFFSET;

        int total = stockData.getBuyCount() + stockData.getSellCount();

        String color = determineVolumeColor(total, stockData.getBuyCount(), stockData.getSellCount());

        return new VolumeDto(stockData.getStockPriceHistoryId(), timestamp, total, color,stockData.getBuyCount(),stockData.getSellCount());
    }


    // --- Private Helper Methods ---

    /**
     * 가짜 StockData 생성 (빈 시간 채우기용)
     */
    private static StockData createFakeStockData(AtomicLong idGen, StockData base, LocalDateTime time) {
        return new StockData(
                idGen.getAndDecrement(),   // 가짜 ID
                base.getEcoStockId(),      // 동일한 종목 ID
                time,                      // 채워넣을 시각
                base.getStockPrice(),      // 직전 가격 유지
                0,                         // 매수/매도 없음
                0
        );
    }

    /**
     * OHLC DTO 생성
     */
    private static OhlcDto createOhlcDto(long prevStockPrice, StockData current) {
        long timestamp = current.getStockTime()
                .atZone(KOREA_ZONE)
                .toEpochSecond() + KST_OFFSET;

        return new OhlcDto(
                current.getStockPriceHistoryId(),
                timestamp,
                prevStockPrice,                                // open
                current.getStockPrice(),                       // high
                current.getStockPrice(),                       //low
                current.getStockPrice(),                       // close
                current.getStockPriceHistoryId() < 0L          // fake 데이터 여부
        );
    }

    public static VolumeDto updateVolumeDto(VolumeDto beforeVolumeDto, StockData stockData) {

        long timestamp = stockData.getStockTime()
                .atZone(KOREA_ZONE)
                .toEpochSecond() + KST_OFFSET;

        if (beforeVolumeDto == null) {
            int total = stockData.getBuyCount() + stockData.getSellCount();

            String color = determineVolumeColor(total, stockData.getBuyCount(), stockData.getSellCount());

            return new VolumeDto(stockData.getStockPriceHistoryId(), timestamp, total, color,stockData.getSellCount(),stockData.getBuyCount());
        }

        int newBuyCount = stockData.getBuyCount()+ beforeVolumeDto.buyCount();
        int newSellCount = stockData.getSellCount()+beforeVolumeDto.sellCount();
        int total = newBuyCount+ newSellCount;
        String color = determineVolumeColor(total, newBuyCount, newSellCount);

        return new VolumeDto(stockData.getStockPriceHistoryId(), timestamp, total, color,newBuyCount,newSellCount);
    }

    /**
     * 볼륨 색상 결정
     */
    private static String determineVolumeColor(int total, int buyCount, int sellCount) {

        if (total == 0) {
            return "EMPTY";
        }
        if (buyCount == sellCount) {
            return "SAME";
        }
        return buyCount > sellCount ? "BUY" : "SELL";
    }

    public static long convertLocalDateTimeToEpoch(LocalDateTime localDateTime) {

        return localDateTime
                .atZone(KOREA_ZONE)
                .toEpochSecond() + KST_OFFSET;
    }
}