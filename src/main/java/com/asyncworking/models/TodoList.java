package com.asyncworking.models;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "todo_list")
@ToString(exclude = "todoItems")
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Project project;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "todoList", cascade = CascadeType.ALL)
    @OrderBy("createdTime DESC")
    private List<TodoItem> todoItems;

    @Column(name = "todo_list_title", nullable = false)
    private String todoListTitle;

    @Column(name = "todo_list_details")
    private String details;

    @Column(name = "origin_notes")
    private String originDetails;

    @Column(name = "doc_url")
    private String docUrl;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

}
