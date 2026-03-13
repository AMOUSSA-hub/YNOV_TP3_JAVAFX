// src/main/java/com/example/tp3/TicketDao.java - À compléter
package com.example.tp3;

import java.util.List;
import java.util.Optional;

// TODO-1 : créer l'interface TicketDao
// TODO-2 : déclarer les méthodes suivantes :
// SupportTicket insert(SupportTicket ticket);
// List<SupportTicket> findAll();
// Optional<SupportTicket> findById(long id);
// List<SupportTicket> searchByTitleOrCustomer(String keyword);
// void update(SupportTicket ticket);
// void deleteById(long id);
// void deleteAll();

public interface TicketDao {
    public SupportTicket insert(SupportTicket ticket);

    public List<SupportTicket> findAll();

    public Optional<SupportTicket> findById(long id);

    public List<SupportTicket> searchByTitleOrCustomer(String keyword);

    public void update(SupportTicket ticket);

    public void deleteById(long id);

    public void deleteAll();
}