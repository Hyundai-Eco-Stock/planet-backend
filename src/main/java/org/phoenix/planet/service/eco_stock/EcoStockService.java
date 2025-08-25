package org.phoenix.planet.service.eco_stock;

import org.phoenix.planet.dto.eco_stock.raw.EcoStock;

import java.util.List;

public interface EcoStockService {

    EcoStock searchById(long ecoStockId);

    List<EcoStock> findAll();
}
