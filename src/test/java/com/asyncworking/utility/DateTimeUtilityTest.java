package com.asyncworking.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DateTimeUtilityTest {
    private DateTimeUtility dateTimeUtility;
    private final OffsetDateTime dayStartTime = OffsetDateTime.now();
    private final OffsetDateTime nextDayStartTime = dayStartTime.plusHours(24);

    @BeforeEach
    public void setup() {
        dateTimeUtility = new DateTimeUtility();
    }

    @Test
    public void overlapWhenPeriodStartsAtStartTimeOfCurrentDay() {
        OffsetDateTime periodStartTime = dayStartTime;
        OffsetDateTime periodEndTime = periodStartTime.plusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void overlapWhenPeriodStartsInCurrentDay() {
        OffsetDateTime periodStartTime = dayStartTime.plusHours(3);
        OffsetDateTime periodEndTime = periodStartTime.plusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void notOverlapWhenPeriodStartsAtStartTimeOfNextDay() {
        OffsetDateTime periodStartTime = nextDayStartTime;
        OffsetDateTime periodEndTime = periodStartTime.plusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(false, isOverlap);
    }

    @Test
    public void overlapWhenPeriodEndsInCurrentDay() {
        OffsetDateTime periodEndTime = dayStartTime.plusHours(12);
        OffsetDateTime periodStartTime = dayStartTime.minusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void overlapWhenPeriodEndsAtTheStartTimeOfNextDay() {
        OffsetDateTime periodEndTime = nextDayStartTime;
        OffsetDateTime periodStartTime = dayStartTime.minusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void notOverlapWhenPeriodEndsAtTheStartTimeOfCurrentDay() {
        OffsetDateTime periodEndTime = dayStartTime;
        OffsetDateTime periodStartTime = dayStartTime.minusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(false, isOverlap);
    }

    @Test
    public void overlapWhenPeriodStartsBeforeStartTimeOfCurrentDayAndEndsAfterStartTimeOfNextDay() {
        OffsetDateTime periodStartTime = dayStartTime.minusHours(10);
        OffsetDateTime periodEndTime = nextDayStartTime.plusHours(8);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }
}
