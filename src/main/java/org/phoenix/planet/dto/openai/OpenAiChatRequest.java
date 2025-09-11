package org.phoenix.planet.dto.openai;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiChatRequest {

    private String model;
    private List<OpenAiMessage> messages;
}
