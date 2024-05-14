package com.wolfcode.eventservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_reviews")
public class Reviews {

    @Id
    @GeneratedValue
    private Long reviewId;
    private String email;
    private int rating;
    private String review;
    private LocalDateTime at;

    @ManyToOne
    @JoinColumn(name = "event", referencedColumnName = "eventCode")
    private Events event;
}
