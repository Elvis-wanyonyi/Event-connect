package com.wolfcode.invitationservice.entity;

import com.wolfcode.invitationservice.dto.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inviteCode;
    private String recipient;
    private String sender;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDateTime sendAt;
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;
    @Column(columnDefinition = "TEXT")
    private String comment;
    private boolean sentReminder;
    private LocalDateTime reminderDate;
}
