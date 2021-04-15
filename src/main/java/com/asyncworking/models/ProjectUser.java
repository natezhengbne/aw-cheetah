package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_user")
public class ProjectUser {

    @EmbeddedId
    @Id
    private ProjectUserId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "attended", nullable = false, columnDefinition = "boolean default true")
    private Boolean attended;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;
}
