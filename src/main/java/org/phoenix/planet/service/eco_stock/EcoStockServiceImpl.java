package org.phoenix.planet.service.eco_stock;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.EcoStockError;
import org.phoenix.planet.constant.SellStockErrorCode;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.error.ecoStock.EcoStockException;
import org.phoenix.planet.mapper.EcoStockMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcoStockServiceImpl implements EcoStockService {

    private final EcoStockMapper ecoStockMapper;

    @Override
    public EcoStock searchById(long ecoStockId) {

        return ecoStockMapper.selectById(ecoStockId);
    }
    @Override
    public List<EcoStock> findAll() {
        return ecoStockMapper.findAll();
    }

    @Override
    public void updateQuantityById(Long stockId, int updateQuantity) {
        ecoStockMapper.updateQuantityById(stockId, updateQuantity);
    }

    @Override
    public List<EcoStockUpdatePriceRecord> findAllHistory(LocalDateTime targetTime) {

        return ecoStockMapper.findAllHistory(targetTime);
    }

    @Override
    public void sellStock(Long memberId, SellStockRequest sellStockRequest) {

        ecoStockMapper.callSellStockProcedure(memberId, sellStockRequest);
        // 프로시저 실행 후, DTO에 채워진 OUT 파라미터 값을 확인
        Integer successCode = sellStockRequest.getPSuccess();

        validateSuccessCode(successCode);

        log.info("판매 성공: {}", sellStockRequest.getPMessage());
    }

    private void validateSuccessCode(Integer successCode) {

        if (successCode == null || successCode != 1) {

            EcoStockError error = Optional.ofNullable(successCode)
                    .filter(code -> code < 0)
                    .map(SellStockErrorCode::getEcoStockError)
                    .orElse(EcoStockError.INTERNAL_SERVER_ERROR);

            throw new EcoStockException(error);
        }
    }
}
