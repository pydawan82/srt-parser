package com.pydawan.srt;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SrtToken {
    private static final String DURATION_FORMAT = "%02d:%02d:%02d,%03d";

    private int order;
    private Duration start;
    private Duration end;
    private String text;

    private String toString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
        long millis = duration.minusHours(hours).minusMinutes(minutes).minusSeconds(seconds).toMillis();

        return String.format(DURATION_FORMAT, hours, minutes, seconds, millis);
    }

    @Override
    public String toString() {
        return String.format("%d\n%s --> %s\n%s\n", order, toString(start), toString(end), text);
    }
}
