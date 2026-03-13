// src/main/java/com/example/tp3/TicketPersistenceService.java - À compléter
package com.example.tp3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class TicketPersistenceService {

    // TODO-1 : déclarer un TicketDao dao = new SQLiteTicketDao()
    // TODO-2 : déclarer une ObservableList<SupportTicket> tickets = FXCollections.observableArrayList()

    // TODO-3 : créer un constructeur qui appelle refresh()

    // TODO-4 : créer getTickets()

    // TODO-5 : créer refresh() pour recharger depuis la base

    // TODO-6 : créer createTicket(SupportTicket ticket)
    // -> insérer via dao, ajouter/recharger la liste, retourner le ticket créé

    // TODO-7 : créer updateTicket(SupportTicket ticket)

    // TODO-8 : créer deleteTicket(long id)

    // TODO-9 : créer deleteAllTickets()

    // TODO-10 : créer search(String keyword) qui retourne une List<SupportTicket>

    private final TicketDao dao = new SQLiteTicketDao();
    private final ObservableList<SupportTicket> tickets = FXCollections.observableArrayList();

    public TicketPersistenceService() {
        refresh();
    }

    public ObservableList<SupportTicket> getTickets() {
        return tickets;
    }

    public SupportTicket createTicket(SupportTicket ticket) {
        SupportTicket created = dao.insert(ticket);
        refresh();
        return created;
    }

    public void updateTicket(SupportTicket ticket) {
        dao.update(ticket);
        refresh();
    }

    public void deleteTicket(long id) {
        dao.deleteById(id);
        refresh();
    }

    public void deleteAllTickets() {
        dao.deleteAll();
        refresh();
    }

    public List<SupportTicket> search(String keyword) {
        return dao.searchByTitleOrCustomer(keyword);
    }

    public void refresh() {
        try {
            List<SupportTicket> allTickets = dao.findAll();
            tickets.setAll(allTickets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}