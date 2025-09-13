// RaffleParticipationByDayItem.java

package org.phoenix.planet.dto.admin.raffle;

public record RaffleParticipationByDayItem(
    String raffleOpenDate,
    Long dailyParticipants
) {

}