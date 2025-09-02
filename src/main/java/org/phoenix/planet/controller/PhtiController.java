package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.phti.raw.PhtiResultResponse;
import org.phoenix.planet.dto.phti.request.PhtiSurveyAnswer;
import org.phoenix.planet.dto.phti.response.PhtiQuestionWithChoicesResponse;
import org.phoenix.planet.service.phti.PhtiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/phti")
public class PhtiController {

    private final PhtiService phtiService;

    @GetMapping("/questions-with-choices")
    public ResponseEntity<List<PhtiQuestionWithChoicesResponse>> fetchPhtiQuestionsWithChoices() {

        List<PhtiQuestionWithChoicesResponse> response = phtiService.fetchAllQuestionsWithChoices();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/surveys")
    public ResponseEntity<?> submitPhtiSurveyChoiceAnswer(
        @LoginMemberId Long memberId,
        @RequestBody PhtiSurveyAnswer phtiSurveyAnswer
    ) {

        log.info("phtiSurveyAnswer:{}", phtiSurveyAnswer);
        PhtiResultResponse phtiResultResponse = phtiService.getResult(memberId, phtiSurveyAnswer);
        return ResponseEntity.ok(phtiResultResponse);
    }
}
