package org.phoenix.planet.dto.member.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointExchangeHistory {

    long id;
    Double pointPrice;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}