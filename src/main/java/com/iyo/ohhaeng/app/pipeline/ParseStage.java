package com.iyo.ohhaeng.app.pipeline;

import com.iyo.ohhaeng.app.command.Command;
import com.iyo.ohhaeng.app.command.CommandParser;
import org.springframework.stereotype.Component;

@Component
public class ParseStage implements Stage {

    private final CommandParser parser;

    public ParseStage(CommandParser parser) {
        this.parser = parser;
    }

    @Override
    public void process(SkillContext ctx) {
        Command command = parser.parse(ctx.normalizedUtterance());
        ctx.command(command);
    }
}
