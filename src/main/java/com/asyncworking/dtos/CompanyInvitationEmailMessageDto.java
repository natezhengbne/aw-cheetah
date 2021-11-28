package com.asyncworking.dtos;

import com.asyncworking.constants.EmailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInvitationEmailMessageDto {
    private Long emailRecordId;
    private String email;
    private String userName;
    private String companyName;
    private String companyOwnerName;
    private String invitationLink;
    private EmailType templateType;
    private String templateS3Bucket;
    private String templateS3Key;
}
