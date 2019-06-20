package org.mundm.wetter.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeHelper {
    private ZoneId zoneId;

    public DateTimeHelper(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public long toEqochSecond(LocalDateTime date) {
        return date
                .atZone(zoneId)
                .toEpochSecond();
    }

    public LocalDateTime fromEpochSecond(long epochSecond) {
        return Instant
                .ofEpochMilli(epochSecond)
                .atZone(zoneId)
                .toLocalDateTime();
    }
}
