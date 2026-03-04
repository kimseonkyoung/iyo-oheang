package com.iyo.ohhaeng.infra.time;

import java.time.Instant;

public interface ClockHolder {
    Instant now();
}
