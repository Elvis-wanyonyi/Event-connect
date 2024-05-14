package com.wolfcode.eventservice.repository;

import com.wolfcode.eventservice.entity.EventMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMediaRepository extends JpaRepository<EventMedia, Long> {


}
