package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "event")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long companyId;

    @Column(nullable = false)
    private Long projectId;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private boolean allDayEvent = false;

    @Column
    private OffsetDateTime startTime;

    @Column
    private OffsetDateTime endTime;

    @Column(nullable = false)
    private OffsetDateTime createdTime;

    @Column(nullable = false)
    private OffsetDateTime updatedTime;
}
