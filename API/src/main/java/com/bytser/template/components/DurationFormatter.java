package com.bytser.template.components;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DurationFormatter {

    private DurationFormatter() {
    }

    public static String format(Duration duration) {
        long seconds = Math.abs(duration.getSeconds());

        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        long secs = seconds % 60;

        List<String> parts = new ArrayList<>();

        if (days > 0) {
            parts.add(days + (days == 1 ? " day" : " days"));
        }
        if (hours > 0) {
            parts.add(hours + (hours == 1 ? " hour" : " hours"));
        }
        if (minutes > 0) {
            parts.add(minutes + (minutes == 1 ? " minute" : " minutes"));
        }
        if (secs > 0 || parts.isEmpty()) {
            parts.add(secs + (secs == 1 ? " second" : " seconds"));
        }

        return String.join(" ", parts);
    }
}
