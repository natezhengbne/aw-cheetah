package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IMessageInfoImpl implements IMessageInfo{
    @Id
    @Type(type = "long")
    private Long Id;
    private String messageTitle;
    private Long posterUserId;
    private String posterUser;
    private String content;
    private Category category;
    private String docURL;
    private OffsetDateTime postTime;

    @Override
    public Long getId() {
        return Id;
    };
    @Override
    public String getMessageTitle() {
        return messageTitle;
    };
    @Override
    public Long getPosterUserId() {
        return posterUserId;
    };
    @Override
    public String getPosterUser() {
        return posterUser;
    };
    @Override
    public String getContent() {
        return content;
    };
    @Override
    public Category getCategory() {
        return category;
    };
    @Override
    public String getDocURL() {
        return docURL;
    };
    @Override
    public OffsetDateTime getPostTime() {
        return postTime;
    };
}
