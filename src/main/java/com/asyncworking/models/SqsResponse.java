package com.asyncworking.models;

import lombok.Data;

@Data
public class SqsResponse {
    private String emailType;
    private String email;
    private String timeSent;
    private String sesResultId;
}
