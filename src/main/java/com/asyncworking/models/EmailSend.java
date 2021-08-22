package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_send")
public class EmailSend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @Column(name = "is_sent")
    private Boolean isSent;

    @Column(name = "update_time", nullable = false)
    private OffsetDateTime updatedTime;
}
