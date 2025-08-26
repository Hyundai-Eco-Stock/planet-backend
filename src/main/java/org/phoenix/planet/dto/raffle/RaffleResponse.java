package org.phoenix.planet.dto.raffle;

import java.time.LocalDate;

public record RaffleResponse(
        Long raffleId,
        Integer ecoStockAmount,
        LocalDate startDate,
        LocalDate endDate,

        Long productId,
        String productName,
        Integer price,
        String imageUrl,

        String brandName,

        Long ecoStockId,
        String ecoStockName
) {}
