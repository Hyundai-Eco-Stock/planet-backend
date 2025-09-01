package org.phoenix.planet.service.phti;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.phti.raw.MemberPhtiSaveRequest;
import org.phoenix.planet.dto.phti.raw.Phti;
import org.phoenix.planet.dto.phti.raw.PhtiQuestionWithChoicesAndAnswer;
import org.phoenix.planet.dto.phti.raw.PhtiResultResponse;
import org.phoenix.planet.dto.phti.request.PhtiSurveyAnswer;
import org.phoenix.planet.dto.phti.request.PhtiSurveyAnswer.Answer;
import org.phoenix.planet.dto.phti.response.ChoiceResponse;
import org.phoenix.planet.dto.phti.response.PhtiQuestionWithChoicesResponse;
import org.phoenix.planet.mapper.MemberPhtiMapper;
import org.phoenix.planet.mapper.PhtiMapper;
import org.phoenix.planet.mapper.PhtiQuestionMapper;
import org.phoenix.planet.util.prompt.PhtiPromptBuilder;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhtiServiceImpl implements PhtiService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final PhtiPromptBuilder phtiPromptBuilder;
    // Mapper
    private final PhtiMapper phtiMapper;
    private final MemberPhtiMapper memberPhtiMapper;
    private final PhtiQuestionMapper phtiQuestionMapper;

    @Override
    public List<PhtiQuestionWithChoicesResponse> fetchAllQuestionsWithChoices() {

        return phtiQuestionMapper.selectQuestionWithChoices();
    }

    @Override
    @Transactional
    public PhtiResultResponse getResult(long memberId, PhtiSurveyAnswer phtiSurveyAnswer) {
        // 1. 소스 만들기
        List<PhtiQuestionWithChoicesAndAnswer> aiRequestSource = parseToAiRequest(phtiSurveyAnswer);
        // 2. AI 응답 받기
        String aiResponse = analyzePhti(aiRequestSource);
        log.info("[AI Response]\n:{}", aiResponse);
        // 3. AI 응답을 json -> class 타입 변환
        PhtiResultResponse phtiResultResponse = parseJsonType(aiResponse);
        // 4. save dto로 변환
        MemberPhtiSaveRequest memberPhtiSaveRequest = parseToSaveRequest(phtiResultResponse,
            memberId);
        // 5. 멤버의 PHTI 정보 저장
        memberPhtiMapper.insertOrUpdate(memberPhtiSaveRequest);
        return phtiResultResponse;
    }

    private List<Phti> fetchAllPhtiList() {

        return phtiMapper.selectAll();
    }

    private PhtiResultResponse parseJsonType(String aiResponse) {

        try {
            return objectMapper.readValue(extractJson(aiResponse),
                PhtiResultResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("PHTI 응답 파싱 실패: " + aiResponse, e);
        }
    }

    private MemberPhtiSaveRequest parseToSaveRequest(PhtiResultResponse response, long memberId) {

        return MemberPhtiSaveRequest
            .builder()
            .primaryPhti(response.primaryPhti())
            .primaryPhtiCustomDescription(response.primaryPhtiCustomDescription())
            .secondaryPhti(response.secondaryPhti())
            .tertiaryPhti(response.tertiaryPhti())
            .ecoChoiceRatio(response.ecoChoiceRatio())
            .valueChoiceRatio(response.valueChoiceRatio())
            .raffleChoiceRatio(response.raffleChoiceRatio())
            .pointChoiceRatio(response.pointChoiceRatio())
            .memberId(memberId)
            .build();
    }

    private List<PhtiQuestionWithChoicesAndAnswer> parseToAiRequest(
        PhtiSurveyAnswer phtiSurveyAnswer) {

        List<PhtiQuestionWithChoicesAndAnswer> questionsWithChoicesAndAnswers =
            fetchAllQuestionsWithChoices().stream()
                .map(q -> {

                    long selectedChoiceId = phtiSurveyAnswer.answers().stream()
                        .filter(a -> a.questionId() == q.getQuestionId())
                        .map(Answer::choiceId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 문제의 답이 없습니다."));

                    String selectedChoiceText = q.getChoices().stream()
                        .filter(c -> c.getChoiceId() == selectedChoiceId)
                        .map(ChoiceResponse::getChoiceText)
                        .findFirst()
                        .orElseThrow(
                            () -> new IllegalArgumentException("선택된 choiceid에 해당하는 텍스트가 없습니다."));

                    return PhtiQuestionWithChoicesAndAnswer.builder()
                        .questionId(q.getQuestionId())
                        .questionOrder(q.getQuestionOrder())
                        .questionText(q.getQuestionText())
                        .questionType(q.getQuestionType())
                        .questionOrder(q.getQuestionOrder())
                        .selectedChoiceId(selectedChoiceId)
                        .selectedChoiceText(selectedChoiceText)
                        .build();
                })
                .toList();
        return questionsWithChoicesAndAnswers;
    }

    private String analyzePhti(List<PhtiQuestionWithChoicesAndAnswer> request) {

        // PHTI 목록
        List<Phti> phtiList = fetchAllPhtiList();

        String systemPrompt = phtiPromptBuilder.buildSystemPrompt(phtiList);
        log.info("[SYSTEM PROMPT] \n{}", systemPrompt);
        String userPrompt = phtiPromptBuilder.buildUserPrompt(request);
        log.info("[USER PROMPT] \n{}", userPrompt);

        // System + User 메시지 구성
        List<Message> messages = List.of(
            SystemMessage.builder().text(systemPrompt).build(),
            UserMessage.builder().text(userPrompt).build()
        );

        return chatModel.call(
                Prompt.builder()
                    .messages(messages)
                    .build())
            .getResult().getOutput().getText();
    }

    private String extractJson(String response) {

        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start != -1 && end != -1 && start < end) {
            return response.substring(start, end + 1);
        }
        throw new IllegalArgumentException("응답에서 JSON을 찾을 수 없습니다: " + response);
    }
}
