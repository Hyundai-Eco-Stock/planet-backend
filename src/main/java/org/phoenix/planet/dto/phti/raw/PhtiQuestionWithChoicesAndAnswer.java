package org.phoenix.planet.dto.phti.raw;

import java.util.List;
import lombok.Builder;
import org.phoenix.planet.dto.phti.response.ChoiceResponse;

@Builder
public record PhtiQuestionWithChoicesAndAnswer(

    long questionId,
    int questionOrder,
    String questionText,
    String questionType,
    List<ChoiceResponse> choices,
    long selectedChoiceId, // 선택한 choiceId
    String selectedChoiceText // 선택한 choiceId
) {

}
