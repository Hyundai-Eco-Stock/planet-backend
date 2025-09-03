package org.phoenix.planet.dto.admin.donation;

import java.util.List;
import lombok.Builder;

@Builder
public record DonationAmountsByDayResponse(
    long totalDonation,
    List<DonationAmountsByDayItem> items
) {

}
