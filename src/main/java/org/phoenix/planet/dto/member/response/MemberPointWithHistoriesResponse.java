package org.phoenix.planet.dto.member.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPointWithHistoriesResponse {

    private long memberId;
    private int currentPoint;
    private List<PointExchangeHistory> histories;

}
