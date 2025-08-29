package org.phoenix.planet.dto.member.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyRaffleResponse {

    private Long raffleHistoryId;
    private String winStatus;
    private Long raffleId;
    private String startDate;
    private String endDate;
    private String productName;
    private String productImageUrl;
}
