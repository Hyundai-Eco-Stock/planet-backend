package org.phoenix.planet.service.phti;

import java.util.List;
import org.phoenix.planet.dto.phti.raw.PhtiResultResponse;
import org.phoenix.planet.dto.phti.request.PhtiSurveyAnswer;
import org.phoenix.planet.dto.phti.response.PhtiQuestionWithChoicesResponse;

public interface PhtiService {

    List<PhtiQuestionWithChoicesResponse> fetchAllQuestionsWithChoices();

    PhtiResultResponse getResult(long memberId, PhtiSurveyAnswer phtiSurveyAnswer);

}
