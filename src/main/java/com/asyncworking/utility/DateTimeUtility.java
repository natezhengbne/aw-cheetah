package com.asyncworking.utility;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@NoArgsConstructor
public class DateTimeUtility {

    public static final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
    public static final long MILLISECONDS_IN_MINUTE = 1000 * 60;

    public boolean isPeriodOverlapADay(OffsetDateTime periodStartTime, OffsetDateTime periodEndTime, OffsetDateTime dayStartTime) {
        final OffsetDateTime nextDayStartTime = dayStartTime.plusHours(24);
        return (!periodStartTime.isBefore(dayStartTime) && periodStartTime.isBefore(nextDayStartTime))
                || (periodEndTime.isAfter(dayStartTime) && !periodEndTime.isAfter(nextDayStartTime))
                || periodStartTime.isBefore(dayStartTime) && periodEndTime.isAfter(nextDayStartTime);
    }
}
