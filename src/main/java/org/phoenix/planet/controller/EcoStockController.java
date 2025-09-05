package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.response.ChartDataResponse;
import org.phoenix.planet.service.eco_stock.EcoStockPriceHistoryService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/eco-stock")
public class EcoStockController {

    private final EcoStockService ecoStockService;
    private final EcoStockPriceHistoryService ecoStockPriceHistoryService;


    @GetMapping("/list")
    public ResponseEntity<?> getEcoStockInfos() {
        List<EcoStock> ecoStockBasicInfos = ecoStockService.findAll();
        return ResponseEntity.ok(ecoStockBasicInfos);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getEcoStockHistory(@RequestParam Long ecoStockId) {

        ChartDataResponse stockDataList = ecoStockPriceHistoryService.findAllPrice(ecoStockId);

        return ResponseEntity.ok(stockDataList);
    }
}
