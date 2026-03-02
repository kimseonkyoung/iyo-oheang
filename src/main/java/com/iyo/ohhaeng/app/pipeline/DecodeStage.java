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
            ctx.callbackUrl(text(root, "/callbackUrl"));

        } catch (Exception e) {
            // JSON 파싱 자체가 실패하면 utterance를 빈 문자열로 설정
            // → NormalizeStage, ParseStage를 거쳐 UNKNOWN Command로 이어짐
            ctx.utterance("");
        }
    }

    private String text(JsonNode root, String pointer) {
        JsonNode node = root.at(pointer);
        return (node.isMissingNode() || node.isNull()) ? null : node.asText();
    }
}
