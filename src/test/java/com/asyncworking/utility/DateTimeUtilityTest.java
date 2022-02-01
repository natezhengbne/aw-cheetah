package com.asyncworking.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DateTimeUtilityTest {

    private final OffsetDateTime dayStartTime = OffsetDateTime.now();
    private final OffsetDateTime nextDayStartTime = dayStartTime.plusHours(24);
    private DateTimeUtility dateTimeUtility;

    @BeforeEach
    public void setup() {
        dateTimeUtility = new DateTimeUtility();
    }

    @Test
    public void testIsPeriodOverlapADayWhenPeriodStartsAtStartTimeOfCurrentDayThenReturnTrue() {
        OffsetDateTime periodStartTime = dayStartTime;
        OffsetDateTime periodEndTime = periodStartTime.plusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void testIsPeriodOverlapADayWhenPeriodStartsInCurrentDayThenReturnTrue() {
        OffsetDateTime periodStartTime = dayStartTime.plusHours(3);
        OffsetDateTime periodEndTime = periodStartTime.plusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void testIsPeriodOverlapADayWhenPeriodStartsAtStartTimeOfNextDayThenReturnFalse() {
        OffsetDateTime periodStartTime = nextDayStartTime;
        OffsetDateTime periodEndTime = periodStartTime.plusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(false, isOverlap);
    }

    @Test
    public void testIsPeriodOverlapADayWhenPeriodEndsInCurrentDayThenReturnTrue() {
        OffsetDateTime periodEndTime = dayStartTime.plusHours(12);
        OffsetDateTime periodStartTime = dayStartTime.minusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void testIsPeriodOverlapADayWhenPeriodEndsAtTheStartTimeOfNextDayThenReturnTrue() {
        OffsetDateTime periodEndTime = nextDayStartTime;
        OffsetDateTime periodStartTime = dayStartTime.minusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }

    @Test
    public void testIsPeriodOverlapADayWhenPeriodEndsAtTheStartTimeOfCurrentDayThenReturnFalse() {
        OffsetDateTime periodEndTime = dayStartTime;
        OffsetDateTime periodStartTime = dayStartTime.minusHours(1);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(false, isOverlap);
    }

    @Test
    public void testIsPeriodOverlapADayWhenPeriodStartsBeforeStartTimeOfCurrentDayAndEndsAfterStartTimeOfNextDayThenReturnTrue() {
        OffsetDateTime periodStartTime = dayStartTime.minusHours(10);
        OffsetDateTime periodEndTime = nextDayStartTime.plusHours(8);

        boolean isOverlap = dateTimeUtility.isPeriodOverlapADay(periodStartTime, periodEndTime, dayStartTime);

        assertEquals(true, isOverlap);
    }
}
