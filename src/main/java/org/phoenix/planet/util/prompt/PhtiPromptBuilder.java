package org.phoenix.planet.util.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.phti.raw.Phti;
import org.phoenix.planet.dto.phti.raw.PhtiQuestionWithChoicesAndAnswer;
import org.phoenix.planet.dto.phti.raw.PhtiResultResponse;
import org.phoenix.planet.dto.phti.response.ChoiceResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhtiPromptBuilder {

    private final ObjectMapper objectMapper;

    public String buildSystemPrompt(List<Phti> phtiList) {

        // 샘플 choice 리스트
        List<ChoiceResponse> sampleChoices = new ArrayList<>(
            Arrays.asList(
                ChoiceResponse.builder()
                    .choiceId(12)
                    .choiceText("거의 매번 참여한다 (주 3회 이상)")
                    .choiceOrder(1)
                    .mappedType("E")
                    .build(),
                ChoiceResponse.builder()
                    .choiceId(13)
                    .choiceText("새로운 에코 이벤트가 나오면 꼭 시도해본다")
                    .choiceOrder(2)
                    .mappedType("E")
                    .build(),
                ChoiceResponse.builder()
                    .choiceId(14)
                    .choiceText("특별한 경우 아니면 잘 참여하지 않는다")
                    .choiceOrder(3)
                    .mappedType("C")
                    .build(),
                ChoiceResponse.builder()
                    .choiceId(15)
                    .choiceText("에코스톡보다는 조용히 포인트 모으는 걸 선호한다")
                    .choiceOrder(4)
                    .mappedType("C")
                    .build()
            )
        );

        // 샘플 질문 리스트
        PhtiQuestionWithChoicesAndAnswer sampleQuestionAndChoicesAndAnswer = PhtiQuestionWithChoicesAndAnswer.builder()
            .questionId(1)
            .questionType("ECO | VALUE | RAFFLE | POINT")
            .questionText("텀블러, 종이백 미사용 같은 친환경 행동에 얼마나 자주 참여하시나요?")
            .choices(sampleChoices)
            .selectedChoiceId(12)
            .selectedChoiceText("거의 매번 참여한다 (주 3회 이상)")
            .build();

        PhtiResultResponse sampleResult = PhtiResultResponse.builder()
            .primaryPhti("최종 1순위 PHTI (예: EGDS)")
            .primaryPhtiCustomDescription("개인화된 2 ~ 3줄의 설명")
            .secondaryPhti("2순위 PHTI")
            .tertiaryPhti("3순위 PHTI")
            .ecoChoiceRatio(75)
            .valueChoiceRatio(100)
            .raffleChoiceRatio(32)
            .pointChoiceRatio(85)
            .build();

        try {
            String sampleInputJson = objectMapper.writeValueAsString(
                sampleQuestionAndChoicesAndAnswer);

            String sampleOutputJson = objectMapper.writeValueAsString(sampleResult);

            StringBuilder sb = new StringBuilder();
            sb.append("너는 사용자의 설문 응답을 분석하여 PHTI(Planet Habit Type Indicator) 결과를 산출하는 시스템이야.\n");
            sb.append("각 성향은 MBTI처럼 두 가지 선택지 중 하나로 결정되며, 최종 PHTI는 4개의 알파벳 조합으로 표현된다.\n");
            sb.append("추가로 각 성향의 선택 비율도 계산한다.\n\n");

            sb.append("- 성향 설명:\n");
            sb.append("1. 에코 소비 성향 (ECO)\n");
            sb.append("탐험가 (E) : Explorer\n");
            sb.append("절약가 (C) : Collector\n\n");

            sb.append("2. 가치 소비 습관 (VALUE)\n");
            sb.append("기부자 (G) : Giver\n");
            sb.append("실속파 (P) : Pragmatist\n\n");

            sb.append("3. 도전 성향 (RAFFLE)\n");
            sb.append("도전자 (D) : Dare\n");
            sb.append("안정파 (A) : Anchored\n\n");

            sb.append("4. 포인트 사용 습관 (POINT)\n");
            sb.append("저축러 (S) : Saver\n");
            sb.append("즉시러 (I) : Immediate\n\n");

            phtiList.forEach(phti ->
                sb.append(phti.phtiName())
                    .append("(").append(phti.phtiAlias()).append(")")
                    .append(": ").append(phti.description()).append("\n")
            );

//            sb.append("EGDS: 환경을 탐험하고, 기부에도 앞장서며, 래플에 도전하지만 포인트는 차곡차곡 모아두는 스타일.\n");
//            sb.append("EGDI: 활동적이고 기부에도 적극적, 래플 도전도 즐기고 포인트는 생기면 바로 써버리는 스타일.\n");
//            sb.append("EGAS: 친환경 활동과 기부는 열심히 하지만 래플보다는 안정적 소비, 포인트는 모아두는 스타일.\n");
//            sb.append("EGAI: 에코와 기부에는 적극적이지만 래플은 피하고, 포인트는 즉시 써버리는 스타일.\n");
//            sb.append("EPDS: 새로운 친환경 활동을 즐기고 실속도 챙기며, 래플에 도전하지만 포인트는 아껴두는 스타일.\n");
//            sb.append("EPDI: 탐험가답게 에코 활동에 적극적이고 실속파이며, 래플도 즐기고 포인트는 즉시 써버리는 스타일.\n");
//            sb.append("EPAS: 실속을 중시하면서도 에코 활동은 적극적, 래플은 피하고 포인트는 모아두는 스타일.\n");
//            sb.append("EPAI: 에코 활동은 열심히 하지만 실속 위주, 래플은 피하고 포인트는 즉시 써버리는 스타일.\n");
//
//            sb.append("CGDS: 특정 에코만 모으면서도 기부에 열정적, 래플에도 도전하지만 포인트는 저축하는 스타일.\n");
//            sb.append("CGDI: 수집가형으로 기부에도 적극적이고, 래플에도 도전하며 포인트는 바로 써버리는 스타일.\n");
//            sb.append("CGAS: 수집형 + 기부형, 래플은 피하지만 포인트는 모아두는 스타일.\n");
//            sb.append("CGAI: 조용히 모으면서 기부는 꾸준히, 래플은 피하지만 포인트는 즉시 써버리는 스타일.\n");
//            sb.append("CPDS: 수집가형 + 실속파, 래플에는 도전하지만 포인트는 모아두는 스타일.\n");
//            sb.append("CPDI: 수집을 즐기면서 실속도 챙기고, 래플에도 도전하며 포인트는 즉시 써버리는 스타일.\n");
//            sb.append("CPAS: 수집형 + 실속파, 래플은 피하고 포인트도 모아두는 스타일.\n");
//            sb.append("CPAI: 수집을 즐기며 실속 위주, 래플은 피하고 포인트는 즉시 써버리는 스타일.\n");

            sb.append("- 규칙\n");
            sb.append("1. 각 성향(E/C, G/P, D/A, S/I)에 대해 가장 높은 비율을 최종 알파벳으로 결정.\n");
            sb.append("2. 최종 PHTI는 네 개의 알파벳을 합쳐서 표현.\n");
            sb.append("3. 2순위, 3순위는 비율이 비슷한 경우 상위 후보를 조합하여 제시.\n");
            sb.append("4. 비율은 소수점 없이 % 단위로 표현.\n\n");

            sb.append("- 입력 데이터 (사용자 응답):\n");
            sb.append(sampleInputJson).append("\n\n");
//                        sb.append("{\n"
//                + "  \"questionId\": 1,\n"
//                + "  \"questionOrder\": 1,\n"
//                + "  \"questionText\": \"텀블러, 종이백 미사용 같은 친환경 행동에 얼마나 자주 참여하시나요?\",\n"
//                + "  \"questionType\": \"ECO | VALUE | RAFFLE | POINT\",\n"
//                + "  \"choices\": [\n"
//                + "    {\"choiceId\": 12, \"choiceText\": \"거의 매번 참여한다 (주 3회 이상)\", \"mappedType\": \"E\"},\n"
//                + "    {\"choiceId\": 13, \"choiceText\": \"새로운 에코 이벤트가 나오면 꼭 시도해본다\", \"mappedType\": \"E\"},\n"
//                + "    {\"choiceId\": 14, \"choiceText\": \"특별한 경우 아니면 잘 참여하지 않는다\", \"mappedType\": \"C\"},\n"
//                + "    {\"choiceId\": 15, \"choiceText\": \"에코스톡보다는 조용히 포인트 모으는 걸 선호한다\", \"mappedType\": \"C\"}\n"
//                + "  ],\n"
//                + "  \"selectedChoiceId\": \"12\"\n"
//                + "  \"selectedChoiceText\": \"거의 매번 참여한다 (주 3회 이상)\"\n"
//                + "}");

            sb.append("- 출력 데이터(JSON 형식):\n");
            sb.append(sampleOutputJson).append("\n\n");
//            sb.append("{\n");
//            sb.append("  \"primaryPHTI\": \"최종 1순위 PHTI (예: EGDS)\",\n");
//            sb.append("  \"secondaryPHTI\": \"2순위 PHTI\",\n");
//            sb.append("  \"tertiaryPHTI\": \"3순위 PHTI\",\n");
//            sb.append("  \"ecoChoiceRatio\": \"E vs C 중 E가 선택된 비율(%)\",\n");
//            sb.append("  \"valueChoiceRatio\": \"G vs P 중 G가 선택된 비율(%)\",\n");
//            sb.append("  \"raffleChoiceRatio\": \"D vs A 중 D가 선택된 비율(%)\",\n");
//            sb.append("  \"pointChoiceRatio\": \"S vs I 중 S가 선택된 비율(%)\"\n");
//            sb.append("}");
            sb.append("반드시 출력 데이터(JSON 형식) **만** 출력해야 해. 다른 설명이나 텍스트는 출력하지 마.\n\n");

            return sb.toString();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("프롬프트 생성 중 JSON 변환 실패", e);
        }
    }

    public String buildUserPrompt(List<PhtiQuestionWithChoicesAndAnswer> request) {

        try {
            String json = objectMapper.writeValueAsString(request);
            return "- 입력 데이터 (사용자 응답):\n" + json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }
}