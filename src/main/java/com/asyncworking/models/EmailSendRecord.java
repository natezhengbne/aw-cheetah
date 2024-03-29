package com.asyncworking.models;

import com.asyncworking.constants.EmailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_send")
public class EmailSendRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "send_status")
    private Boolean sendStatus;

    @Column(name = "email_type")
    @Enumerated(EnumType.STRING)
    private EmailType emailType;

    @JoinColumn(name = "company_id")
    private Long companyId;

    @Column(name = "send_time", nullable = false)
    private OffsetDateTime sendTime;

    @Column(name = "receive_time")
    private OffsetDateTime receiveTime;

}
