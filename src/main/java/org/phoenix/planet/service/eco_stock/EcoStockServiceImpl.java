package org.phoenix.planet.service.eco_stock;

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
    public List<EcoStockUpdatePriceRecord> findAllHistory(String targetTime) {

        return ecoStockMapper.findAllHistory(targetTime);
    }
}
