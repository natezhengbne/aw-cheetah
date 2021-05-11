package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

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
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "message_board_id",
            referencedColumnName = "id",
            nullable = false
    )
    MessageBoard messageBoard;

    @OneToMany(
            mappedBy = "messageBoard",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Set<Message> messages;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "poster_user_id")
    private Long projectUserId;

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
