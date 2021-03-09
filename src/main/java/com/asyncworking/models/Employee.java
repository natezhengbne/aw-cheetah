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
@Table(name = "company_user")
public class Employee {

    @EmbeddedId
    @Id
    private EmployeeId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "title")
    private String title;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

}
