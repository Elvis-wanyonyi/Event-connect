package com.wolfcode.eventservice.repository;

import com.wolfcode.eventservice.entity.Reviews;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, Long> {

    List<Reviews> findByEvent_EventCode(String eventCode);

    void deleteByEmail(String email);
}
