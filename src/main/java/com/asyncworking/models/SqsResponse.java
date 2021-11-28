package com.asyncworking.models;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SqsResponse {
    private Long emailRecordId;
    private String emailType;
    private String email;
    private String timeReceived;
    private String sesResultId;
}
