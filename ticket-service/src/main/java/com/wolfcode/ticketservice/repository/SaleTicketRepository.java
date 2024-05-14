package com.wolfcode.ticketservice.repository;

import com.wolfcode.ticketservice.entity.TicketSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleTicketRepository extends JpaRepository<TicketSale, Long> {
    TicketSale findBySerialNo(String serialNo);

    void deleteBySaleCode(String saleCode);

    TicketSale findBySaleCode(String saleCode);

    List<TicketSale> findByTicket_ticketCode(String ticketCode);

    List<TicketSale> findByEmail(String email);
}
