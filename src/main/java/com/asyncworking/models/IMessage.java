package com.asyncworking.models;

import lombok.Data;
import javax.persistence.Id;

import javax.persistence.Entity;
import java.time.OffsetDateTime;

@Entity
@Data
public class IMessage {
    @Id
    Long id;
    String messageTitle;
    Long posterUserId;
    String posterUser;
    String content;
    Category category;
    String docURL;
    OffsetDateTime postTime;


}
