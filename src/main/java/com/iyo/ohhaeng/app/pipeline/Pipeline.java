package com.iyo.ohhaeng.app.pipeline;

import java.util.List;

public class Pipeline {

    private final List<Stage> stages;

    public Pipeline(List<Stage> stages) {
        this.stages = List.copyOf(stages);
    }

    public SkillContext run(SkillContext ctx) {
        for (Stage stage : stages) {
            stage.process(ctx);
        }
        return ctx;
    }
}
