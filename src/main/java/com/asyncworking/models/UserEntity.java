package com.asyncworking.models;

import java.time.OffsetDateTime;
import javax.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    @Column(name = "created_time")
    private OffsetDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private OffsetDateTime updatedTime;
}
