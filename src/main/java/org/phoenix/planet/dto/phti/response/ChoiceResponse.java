package org.phoenix.planet.dto.phti.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceResponse {

    private long choiceId;
    private String choiceText;
    private String mappedType;
    private int choiceOrder;
}