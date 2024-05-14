package com.wolfcode.eventservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_files")
public class EventMedia {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "event", referencedColumnName = "eventCode")
    private Events event;
}
