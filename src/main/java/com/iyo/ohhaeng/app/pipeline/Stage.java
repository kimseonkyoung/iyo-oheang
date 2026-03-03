package com.iyo.ohhaeng.app.pipeline;

@FunctionalInterface
public interface Stage {
    void process(SkillContext ctx);
}
