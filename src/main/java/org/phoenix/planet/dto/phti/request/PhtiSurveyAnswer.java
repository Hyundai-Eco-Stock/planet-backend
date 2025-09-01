package org.phoenix.planet.dto.phti.request;

import java.util.List;

public record PhtiSurveyAnswer(
    List<Answer> answers
) {

    public record Answer(
        long questionId,
        long choiceId
    ) {

    }
}