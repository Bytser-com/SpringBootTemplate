package com.bytser.template.components;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class DurationFormatterTest {

    @Test
    void format_zeroDuration_returns0Seconds() {
        Duration duration = Duration.ZERO;

        String result = DurationFormatter.format(duration);

        assertEquals("0 seconds", result);
    }

    @Test
    void format_69Seconds_returns1Minute9Seconds() {
        Duration duration = Duration.ofSeconds(69);

        String result = DurationFormatter.format(duration);

        assertEquals("1 minute 9 seconds", result);
    }

    @Test
    void format_SingularAndPluralForms() {
        Duration durationSingular = Duration.ofDays(1).plusHours(1).plusMinutes(1).plusSeconds(1);
        Duration durationPlural = Duration.ofDays(4).plusHours(4).plusMinutes(4).plusSeconds(4);

        String resultSingular = DurationFormatter.format(durationSingular);
        String resultPlural = DurationFormatter.format(durationPlural);

        assertEquals("1 day 1 hour 1 minute 1 second", resultSingular);
        assertEquals("4 days 4 hours 4 minutes 4 seconds", resultPlural);
    }

    @Test
    void format_useBiggestPossibleForm() {
        Duration durationDays = Duration.ofDays(2);
        Duration durationHours = Duration.ofHours(2); 
        Duration durationMinutes = Duration.ofMinutes(2);
        Duration durationSeconds = Duration.ofSeconds(2);

        String resultDays = DurationFormatter.format(durationDays);
        String resultHours = DurationFormatter.format(durationHours);
        String resultMinutes = DurationFormatter.format(durationMinutes);
        String resultSeconds = DurationFormatter.format(durationSeconds);
        
        assertEquals("2 days", resultDays);
        assertEquals("2 hours", resultHours);
        assertEquals("2 minutes", resultMinutes);
        assertEquals("2 seconds", resultSeconds);
    }

    @Test
    void format_singularAndPluralFormsCombined() {
        assertEquals("1 minute 2 seconds", DurationFormatter.format(Duration.ofMinutes(1).plusSeconds(2)));
        assertEquals("2 minutes 1 second", DurationFormatter.format(Duration.ofMinutes(2).plusSeconds(1)));
        assertEquals("1 hour 2 minutes", DurationFormatter.format(Duration.ofHours(1).plusMinutes(2)));
        assertEquals("2 hours 1 minute", DurationFormatter.format(Duration.ofHours(2).plusMinutes(1)));
        assertEquals("1 day 2 hours", DurationFormatter.format(Duration.ofDays(1).plusHours(2)));
        assertEquals("2 days 1 hour", DurationFormatter.format(Duration.ofDays(2).plusHours(1)));
    }
}
