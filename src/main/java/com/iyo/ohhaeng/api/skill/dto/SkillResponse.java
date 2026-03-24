package com.iyo.ohhaeng.api.skill.dto;

import java.util.List;

/**
 * 카카오 스킬 응답 DTO
 * https://kakaobusiness.gitbook.io/main/tool/chatbot/skill_guide/response_format
 */
public class SkillResponse {

    private final String version;
    private final Template template;

    private SkillResponse(String version, Template template) {
        this.version = version;
        this.template = template;
    }

    public static SkillResponse ofSimpleText(String text) {
        SimpleText simpleText = new SimpleText(text);
        Output output = new Output(simpleText);
        Template template = new Template(List.of(output));
        return new SkillResponse("2.0", template);
    }

    public String getVersion() {
        return version;
    }

    public Template getTemplate() {
        return template;
    }

    // --- Nested Static Classes ---

    public static class Template {
        private final List<Output> outputs;

        public Template(List<Output> outputs) {
            this.outputs = outputs;
        }

        public List<Output> getOutputs() {
            return outputs;
        }
    }

    public static class Output {
        private final SimpleText simpleText;

        public Output(SimpleText simpleText) {
            this.simpleText = simpleText;
        }

        public SimpleText getSimpleText() {
            return simpleText;
        }
    }

    public static class SimpleText {
        private final String text;

        public SimpleText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
