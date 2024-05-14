package com.wolfcode.eventservice.repository;

import com.wolfcode.eventservice.dto.EventCategory;
import com.wolfcode.eventservice.entity.Events;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
    Events findByEventName(String name);

    Optional<Events> findByEventCode(String eventCode);

    Page<Events> findAllByEventCategory(EventCategory eventCategory, Pageable pageable);

    void deleteByEventCode(String eventCode);

    Page<Events> findByLocation(String location, Pageable pageable);

    Page<Events> findByDate(LocalDate date, Pageable pageable);

    Page<Events> findByDateBetween(LocalDate nextSaturday, LocalDate nextSunday, Pageable pageable);


    long countByDateAfterOrDateEquals(LocalDate date, LocalDate date2);

    Page<Events> findByOrganizer(String organizer, Pageable pageable);

    Page<Events> findAllByDateAfter(LocalDate today, Pageable pageable);

    Page<Events> findAllByDateBefore(LocalDate today, Pageable pageable);
}
