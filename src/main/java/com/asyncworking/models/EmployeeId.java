package com.asyncworking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "company_id")
    private Long companyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmployeeId that = (EmployeeId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(companyId, that.companyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, companyId);
    }
}
