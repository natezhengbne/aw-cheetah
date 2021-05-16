package com.asyncworking.models;


import lombok.*;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "todo_list")
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Project project;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "todoList")
    @EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<TodoItem> todoItems;

    @Column(name = "todo_list_title", nullable = false)
    private String todoListTitle;

    @Column(name = "todo_list_details")
    private String details;

    @Column(name = "doc_url")
    private String docURL;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

}
