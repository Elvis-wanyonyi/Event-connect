package com.wolfcode.ticketservice.repository;

import com.wolfcode.ticketservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findByTicketCodeIgnoreCase(String ticketCode);

    void deleteByTicketCode(String ticketCode);

    List<Ticket> findByEventCode(String eventCode);
}
