package org.phoenix.planet.dto.phti.raw;

import lombok.Builder;

@Builder
public record MemberPhtiSaveRequest(
    String primaryPhti,
    String primaryPhtiCustomDescription,
    String secondaryPhti,
    String tertiaryPhti,
    Integer ecoChoiceRatio,
    Integer valueChoiceRatio,
    Integer raffleChoiceRatio,
    Integer pointChoiceRatio,
    long memberId
) {

}
