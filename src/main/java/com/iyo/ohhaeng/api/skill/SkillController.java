package com.iyo.ohhaeng.api.skill;

import com.iyo.ohhaeng.api.skill.dto.SkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {

    private final SkillFacade skillFacade;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public SkillResponse handleSkill(
            @RequestBody String rawJson,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId
    ) {
        return skillFacade.process(rawJson, requestId);
    }
}
