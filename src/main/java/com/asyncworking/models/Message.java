package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id",
            nullable = false
    )
    Project project;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "poster_user_id")
    private Long posterUserId;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "message_title", nullable = false)
    private String messageTitle;

    @Column(name = "doc_url")
    private String docURL;

    @Column
    private String content;

    @Column(name = "post_time")
    private OffsetDateTime postTime;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;
}
