package com.iyo.ohhaeng.api.skill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyo.ohhaeng.api.skill.dto.SkillResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkillFacade {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 스킬 요청 처리 (V0: 고정 응답 반환)
     * 추후 파이프라인 연결 예정
     */
    public SkillResponse process(String rawJson, String requestId) {
        logRequestSummary(rawJson, requestId);

        // V0: 고정 응답
        String message = "요청을 받았습니다.";
        return SkillResponse.ofSimpleText(message);
    }

    private void logRequestSummary(String rawJson, String requestId) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            String utterance = extractText(root, "/userRequest/utterance");
            String userId = extractText(root, "/userRequest/user/id");
            boolean hasCallback = root.has("callbackUrl");

            log.info("[Skill] requestId={}, utterance={}, userId={}, hasCallback={}",
                    requestId, utterance, userId, hasCallback);

        } catch (Exception e) {
            log.warn("[Skill] Failed to parse request: requestId={}, error={}",
                    requestId, e.getMessage());
        }
    }

    private String extractText(JsonNode root, String jsonPointer) {
        JsonNode node = root.at(jsonPointer);
        return node.isMissingNode() ? null : node.asText();
    }
}
