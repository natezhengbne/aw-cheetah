package com.asyncworking.models;

import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserEntity> participants;

    @Column(name = "all_day_event")
    private boolean allDayEvent = false;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

    public boolean isWithinDay(OffsetDateTime dayStartTime){
        final OffsetDateTime dayEndTime = dayStartTime.plusHours(24);
        return  (!startTime.isBefore(dayStartTime) && startTime.isBefore(dayEndTime))
                || (endTime.isAfter(dayStartTime) && !endTime.isAfter(dayEndTime));
    }
}
