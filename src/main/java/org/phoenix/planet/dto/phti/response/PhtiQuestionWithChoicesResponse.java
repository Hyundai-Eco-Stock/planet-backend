package org.phoenix.planet.dto.phti.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhtiQuestionWithChoicesResponse {

    private long questionId;
    private int questionOrder;
    private String questionText;
    private String questionType;
    private List<ChoiceResponse> choices;

}
