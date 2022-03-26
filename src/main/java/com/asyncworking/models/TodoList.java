package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Entity
@Data
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Project project;

    @OneToMany(mappedBy = "todoList", cascade = CascadeType.ALL)
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
