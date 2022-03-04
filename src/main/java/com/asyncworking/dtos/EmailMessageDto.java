package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDto {
    private Long emailRecordId;
    private String receiverEmail;
    private String userName;
    private String linkToSend;
    private String companyName;
    private String companyOwnerName;
    private String templateType;
    private String templateS3Bucket;
    private String templateS3Key;
}
