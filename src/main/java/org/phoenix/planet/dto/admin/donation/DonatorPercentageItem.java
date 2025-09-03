package org.phoenix.planet.dto.admin.donation;

public record DonatorPercentageItem(
    String name,  // 참여 / 미참여
    long users,
    String color
) {

}