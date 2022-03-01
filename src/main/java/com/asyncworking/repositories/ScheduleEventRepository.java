package com.asyncworking.repositories;

import com.asyncworking.models.ScheduleEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleEventRepository extends JpaRepository<ScheduleEvent, Long> {

    List<ScheduleEvent> findByCompanyIdAndProjectIdAndOwnerId(Long companyId, Long projectId, Long ownerId);
}
