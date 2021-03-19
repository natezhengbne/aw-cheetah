package com.asyncworking.models;

import lombok.*;
import org.hibernate.annotations.Type;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "company")
public class ImplementICompanyInfo implements ICompanyInfo {
    @Id
    @Type(type = "long")
    private Long id;
    private String name;
    private String description;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
