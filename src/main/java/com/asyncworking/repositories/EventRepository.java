package com.asyncworking.repositories;

import com.asyncworking.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCompanyIdAndProjectIdAndOwnerId(Long companyId, Long projectId, Long ownerId);
}
