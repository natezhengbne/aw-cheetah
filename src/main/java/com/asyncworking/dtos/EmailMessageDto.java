package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDto {
    private Long emailRecordId;
    private String receiverEmail;
    private String userName;
    private String companyName;
    private String companyOwnerName;
    private String linkToSend;
    private String templateType;
    private String templateS3Bucket;
    private String templateS3Key;
}
