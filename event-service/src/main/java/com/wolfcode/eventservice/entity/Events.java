package com.wolfcode.eventservice.entity;

import com.wolfcode.eventservice.dto.EventCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String eventCode;
    private String eventName;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String organizer;
    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedAt;

    @OneToMany(mappedBy = "event")
    private List<Reviews> reviews;

}

