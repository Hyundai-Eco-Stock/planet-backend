// RaffleParticipationItem.java

package org.phoenix.planet.dto.admin.raffle;

public record RaffleParticipationItem(
    Long raffleId,
    String productName,
    Long participants
) {

}