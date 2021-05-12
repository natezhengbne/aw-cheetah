package com.asyncworking.models;

import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "todo_item")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoItemId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", referencedColumnName = "id", nullable = false)
    TodoList todoList;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "todo_list_id", nullable = false)  //how to get it?
    private String todoListId;

    @Column(name = "event_doc_url")
    private String eventDocUrl;

    @Column(name = "content")
    private String content;

    @Column(name = "doc_url")
    private String docUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

}
