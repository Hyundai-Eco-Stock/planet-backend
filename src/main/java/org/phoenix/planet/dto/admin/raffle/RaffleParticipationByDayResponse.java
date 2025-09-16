// RaffleParticipationByDayResponse.java

package org.phoenix.planet.dto.admin.raffle;

import java.util.List;
import lombok.Builder;

@Builder
public record RaffleParticipationByDayResponse(
    List<RaffleParticipationByDayItem> items
) {

}