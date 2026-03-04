package com.iyo.ohhaeng.infra.time;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SystemClockHolder implements ClockHolder {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
