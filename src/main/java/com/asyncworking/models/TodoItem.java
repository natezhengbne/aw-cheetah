package com.asyncworking.models;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "todo_item")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", referencedColumnName = "id", nullable = false)
    private TodoList todoList;

    @Column(name = "user_id", nullable = false)
    private Long createdUserId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "event_doc_url")
    private String eventDocUrl;

    @Column(name = "notes")
    private String notes;

    @Column(name = "origin_notes")
    private String originNotes;

    @Column(name = "doc_url")
    private String docUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

    @Column(name = "subscribers_ids")
    private String subscribersIds;
}
