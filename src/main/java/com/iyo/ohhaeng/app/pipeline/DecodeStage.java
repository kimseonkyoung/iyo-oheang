package com.iyo.ohhaeng.app.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DecodeStage implements Stage {

    private final ObjectMapper objectMapper;

    public DecodeStage(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(SkillContext ctx) {
        try {
            JsonNode root = objectMapper.readTree(ctx.rawJson());

            ctx.utterance(text(root, "/userRequest/utterance"));
            ctx.userId(text(root, "/userRequest/user/id"));

            // callbackUrl: 루트 레벨 우선, 없으면 userRequest 하위 확인
            String callbackUrl = text(root, "/callbackUrl");
            if (callbackUrl == null) callbackUrl = text(root, "/userRequest/callbackUrl");
            ctx.callbackUrl(callbackUrl);

        } catch (Exception e) {
            // JSON 파싱 실패 → 파이프라인 중단, Facade에서 기본 응답 반환
            ctx.fail("JSON_PARSE_ERROR");
        }
    }

    private String text(JsonNode root, String pointer) {
        JsonNode node = root.at(pointer);
        return (node.isMissingNode() || node.isNull()) ? null : node.asText();
    }
}
