package com.asyncworking.models;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "leader_id", nullable = false)
    private Long leaderId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "description")
    private String description;

    @Column(name = "is_private", nullable = false, columnDefinition = "boolean default false")
    private Boolean isPrivate;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

    @OneToMany(mappedBy = "project",
            cascade = CascadeType.ALL)
    private Set<ProjectUser> projectUsers;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Set<Message> messageSet;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Set<MessageCategory> messageCategorySet;
    @OneToMany(mappedBy = "project",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TodoList> todoLists;

    public void addProjectUser(ProjectUser projectUser) {
        projectUsers.add(projectUser);
    }

    public void removeProjectUser(ProjectUser projectUser) {
        projectUsers.remove(projectUser);
    }

    public void addTodoList(TodoList todoList) {
        todoLists.add(todoList);
    }

    public void removeTodoList(TodoList todoList) {
        todoLists.remove(todoList);
    }
}
