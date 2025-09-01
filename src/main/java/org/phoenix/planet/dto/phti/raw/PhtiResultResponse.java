package org.phoenix.planet.dto.phti.raw;

import lombok.Builder;

@Builder
public record PhtiResultResponse(
    String primaryPhti,    // 최종 1순위 PHTI
    String primaryPhtiCustomDescription,
    String secondaryPhti,  // 2순위 PHTI
    String tertiaryPhti,   // 3순위 PHTI
    Integer ecoChoiceRatio,    // E 비율 (%)
    Integer valueChoiceRatio,  // G 비율 (%)
    Integer raffleChoiceRatio, // D 비율 (%)
    Integer pointChoiceRatio   // S 비율 (%)
) {

}