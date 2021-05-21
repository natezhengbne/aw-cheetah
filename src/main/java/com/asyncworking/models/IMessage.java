package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

import javax.persistence.Entity;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
