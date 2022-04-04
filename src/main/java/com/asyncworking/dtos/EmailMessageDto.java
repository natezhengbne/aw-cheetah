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
    private String email;
    private String userName;
    private String companyName;
    private String companyOwnerName;
    private String verificationLink;
    private String invitationLink;
    private String templateType;
    private String templateS3Bucket;
    private String templateS3Key;
}
