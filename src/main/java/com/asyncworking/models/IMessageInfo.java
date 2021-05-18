package com.asyncworking.models;

import java.time.OffsetDateTime;
import java.util.Date;

public interface IMessageInfo {
    Long getId();
    String getMessageTitle();
    Long getPosterUserId();
    String getPosterUser();
    String getContent();
    Category getCategory();
    String getDocURL();
//    OffsetDateTime getPostTime();

}
