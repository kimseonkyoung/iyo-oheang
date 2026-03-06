package com.iyo.ohhaeng.api.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyo.ohhaeng.app.command.CommandParser;
import com.iyo.ohhaeng.app.pipeline.*;
import com.iyo.ohhaeng.app.usecase.GetMyInfoUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SkillControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DecodeStage decodeStage = new DecodeStage(new ObjectMapper());
        NormalizeStage normalizeStage = new NormalizeStage();
        IdempotencyStageNoop idempotencyStageNoop = new IdempotencyStageNoop();
        ParseStage parseStage = new ParseStage(new CommandParser());
        RateLimitStageNoop rateLimitStageNoop = new RateLimitStageNoop();

        GetMyInfoUseCase getMyInfoUseCase = mock(GetMyInfoUseCase.class);
        when(getMyInfoUseCase.execute(anyString())).thenReturn("[내 정보]\n속성: WOOD  강화: +0\nHP: 100/100  스태미나: 50/50");

        SkillFacade skillFacade = new SkillFacade(
                decodeStage, normalizeStage, idempotencyStageNoop, parseStage, rateLimitStageNoop,
                getMyInfoUseCase);

        mockMvc = MockMvcBuilders.standaloneSetup(new SkillController(skillFacade)).build();
    }

    @Test
    @DisplayName("POST /skill - 카카오 스킬 응답 형식 검증")
    void handleSkill_returnsKakaoSkillFormat() throws Exception {
        String body = """
                {
                    "userRequest": {
                        "utterance": "/내정보",
                        "user": {"id": "user-001"}
                    }
                }
                """;

        mockMvc.perform(post("/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Request-Id", "req-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.version").value("2.0"))
                .andExpect(jsonPath("$.template.outputs[0].simpleText.payload.text").isString());
    }

    @Test
    @DisplayName("POST /skill - X-Request-Id 헤더 없이도 동작")
    void handleSkill_withoutRequestIdHeader() throws Exception {
        String body = """
                {"userRequest": {"utterance": "/사냥", "user": {"id": "user-002"}}}
                """;

        mockMvc.perform(post("/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("2.0"))
                .andExpect(jsonPath("$.template.outputs").isArray())
                .andExpect(jsonPath("$.template.outputs[0].simpleText.payload.text").isString());
    }

    @Test
    @DisplayName("POST /skill - 깨진 JSON → 500 금지, 기본 응답 반환")
    void handleSkill_brokenJson_returns200WithFallback() throws Exception {
        mockMvc.perform(post("/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ broken json !!!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("2.0"))
                .andExpect(jsonPath("$.template.outputs[0].simpleText.payload.text")
                        .value("요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요."));
    }

    @Test
    @DisplayName("POST /skill - 알 수 없는 명령어도 200 응답")
    void handleSkill_unknownCommand_returns200() throws Exception {
        String body = """
                {"userRequest": {"utterance": "뭔가이상한말", "user": {"id": "user-003"}}}
                """;

        mockMvc.perform(post("/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.template.outputs[0].simpleText.payload.text")
                        .value("알 수 없는 명령어예요."));
    }

    @Test
    @DisplayName("POST /skill - /대결 @상대 → 파이프라인 전체 통과, 200 응답")
    void handleSkill_duelCommand_pipelinePassthrough() throws Exception {
        String body = """
                {
                    "userRequest": {
                        "utterance": "/대결 @홍길동",
                        "user": {"id": "user-004"}
                    }
                }
                """;

        mockMvc.perform(post("/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("2.0"))
                .andExpect(jsonPath("$.template.outputs[0].simpleText.payload.text")
                        .value("[대결] 준비 중입니다."));
    }
}
