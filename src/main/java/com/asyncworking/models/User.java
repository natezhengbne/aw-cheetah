package com.asyncworking.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "user", schema = "public")
public class User {
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
    @Column(nullable = false)
    private Date createdTime;

    @CreatedDate
    @Column(nullable = false)
    private Date updatedTime;
}
