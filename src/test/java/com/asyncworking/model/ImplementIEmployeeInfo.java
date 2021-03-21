package com.asyncworking.model;

import com.asyncworking.models.IEmployeeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_user")
public class ImplementIEmployeeInfo implements IEmployeeInfo {
    @Id
    @Type(type = "long")
    private String email;
    private String name;
    private String title;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
