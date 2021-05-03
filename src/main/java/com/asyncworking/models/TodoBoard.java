package com.asyncworking.models;


import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todo_board")
public class TodoBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id"
    )
    private Project project;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private OffsetDateTime updatedTime;

    @OneToMany(
            mappedBy = "todoBoard",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Set<TodoList> todoLists;

    public void addTodoList(TodoList todoList) {
        todoLists.add(todoList);
    }

    public void removeTodoList(TodoList todoList) {
        todoLists.remove(todoList);
    }

}
