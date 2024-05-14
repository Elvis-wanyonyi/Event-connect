package com.wolfcode.ticketservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket_sales")
@EntityListeners(AuditingEntityListener.class)
public class TicketSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String saleCode;
    private String serialNo;
    private String email;
    private String ticketType;
    private String amount;
    private String phone;
    private String event;
    private LocalDateTime boughtAt;

    private boolean used;
    private boolean revoked;
    private LocalDateTime checkInTime;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket", referencedColumnName = "ticketCode")
    private Ticket ticket;
}
