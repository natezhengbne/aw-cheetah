package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICompanyInvitationEmailCompanyInfoImpl implements ICompanyInvitationEmailCompanyInfo{
    @Id
    @Type(type = "long")
    private Long companyId;
    private String companyName;
    private String companyOwnerName;

    @Override
    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String getCompanyOwnerName() {
        return companyOwnerName;
    }
}
