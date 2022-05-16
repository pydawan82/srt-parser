package com.pydawan.util;

import java.time.Duration;

public class DurationUtil {
    public static double ratio(Duration duration1, Duration duration2) {
        return duration1.toNanos() / (double) duration2.toNanos();
    }

    public static Duration scale(Duration duration, double ratio) {
        return Duration.ofNanos((long) (duration.toNanos() * ratio));
    }
}
