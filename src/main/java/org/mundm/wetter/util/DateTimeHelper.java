package org.mundm.wetter.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeHelper {
    public long toEqochSecond(LocalDateTime date) {
        return date
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();
    }

    public LocalDateTime fromEpochSecond(long epochSecond) {
        return Instant
                .ofEpochMilli(epochSecond)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
