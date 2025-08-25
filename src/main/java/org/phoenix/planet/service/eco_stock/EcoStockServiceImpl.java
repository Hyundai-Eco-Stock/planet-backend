package org.phoenix.planet.service.eco_stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.mapper.EcoStockMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
