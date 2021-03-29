package com.asyncworking.models;

import lombok.*;
import org.hibernate.annotations.Type;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company")
public class ICompanyInfoImpl implements ICompanyInfo {
    @Id
    @Type(type = "long")
    private Long companyId;
    private String name;
    private String description;

    @Override
    public Long getId() {
        return companyId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
