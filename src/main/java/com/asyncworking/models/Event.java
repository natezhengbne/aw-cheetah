package com.asyncworking.models;

import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "all_day_event")
    private boolean allDayEvent = false;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    public boolean isWithinDay(OffsetDateTime dayStartTime){
        final OffsetDateTime dayEndTime = dayStartTime.plusHours(24);
        return  (!startTime.isBefore(dayStartTime) && startTime.isBefore(dayEndTime))
                || (endTime.isAfter(dayStartTime) && !endTime.isAfter(dayEndTime));
    }
}
