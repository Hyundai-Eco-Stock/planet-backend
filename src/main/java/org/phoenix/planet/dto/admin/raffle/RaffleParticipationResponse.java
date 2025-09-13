// RaffleParticipationResponse.java

package org.phoenix.planet.dto.admin.raffle;

import java.util.List;
import lombok.Builder;

@Builder
public record RaffleParticipationResponse(
    List<RaffleParticipationItem> items
) {

}