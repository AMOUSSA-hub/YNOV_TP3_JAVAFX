// src/main/java/com/example/tp3/TicketDaoTests.java - À compléter
package com.example.tp3;

import java.time.LocalDate;

public class TicketDaoTests {

    public static void main(String[] args) {
        try {
            // TODO-1 : initialiser la base
            System.out.println("=== Initializing database ===");
            DatabaseManager.initializeDatabase();
            System.out.println("Database initialized successfully\n");
            
            // TODO-2 : créer un SQLiteTicketDao
            System.out.println("=== Creating SQLiteTicketDao ===");
            TicketDao dao = new SQLiteTicketDao();
            System.out.println("DAO created successfully\n");
            
            // TODO-3 : vider la table pour partir proprement
            System.out.println("=== Clearing all tickets ===");
            dao.deleteAll();
            System.out.println("All tickets deleted\n");
            
            // TODO-4 : insérer un ticket
            System.out.println("=== Inserting a ticket ===");
            SupportTicket ticket = new SupportTicket(
                    "Cannot login",
                    "John Doe",
                    "HIGH",
                    LocalDate.now(),
                    "User cannot access the application",
                    true,
                    "OPEN"
            );
            SupportTicket inserted = dao.insert(ticket);
            System.out.println("Inserted ticket: " + inserted + "\n");
            
            // TODO-5 : vérifier que l'id est généré
            System.out.println("=== Verifying generated ID ===");
            assert inserted.getId() > 0 : "ID should be greater than 0";
            System.out.println("ID generated correctly: " + inserted.getId() + "\n");
            
            // TODO-6 : vérifier que findAll() retourne au moins un ticket
            System.out.println("=== Verifying findAll() ===");
            var allTickets = dao.findAll();
            System.out.println("Number of tickets: " + allTickets.size());
            assert allTickets.size() >= 1 : "Should have at least 1 ticket";
            for (SupportTicket t : allTickets) {
                System.out.println("  - " + t);
            }
            System.out.println();
            
            // TODO-7 : modifier le ticket puis vérifier la mise à jour
            System.out.println("=== Updating the ticket ===");
            SupportTicket updatedTicket = new SupportTicket(
                    inserted.getId(),
                    "Cannot login - UPDATED",
                    "John Doe",
                    "CRITICAL",
                    inserted.getCreatedAt(),
                    "User cannot access the application - ESCALATED",
                    inserted.isUrgent(),
                    "IN_PROGRESS"
            );
            dao.update(updatedTicket);
            var findResult = dao.findById(inserted.getId());
            assert findResult.isPresent() : "Ticket should be found";
            SupportTicket foundTicket = findResult.get();
            System.out.println("Updated ticket: " + foundTicket);
            assert foundTicket.getTitle().equals("Cannot login - UPDATED") : "Title should be updated";
            assert foundTicket.getPriority().equals("CRITICAL") : "Priority should be updated";
            System.out.println("Update verified successfully\n");
            
            // TODO-8 : supprimer le ticket puis vérifier que la table est vide
            System.out.println("=== Deleting the ticket ===");
            dao.deleteById(inserted.getId());
            var remainingTickets = dao.findAll();
            System.out.println("Number of remaining tickets: " + remainingTickets.size());
            assert remainingTickets.size() == 0 : "Table should be empty";
            System.out.println("Deletion verified successfully\n");
            
            // TODO-9 : afficher un message de succès
            System.out.println("=== ALL TESTS PASSED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            System.err.println("Error during tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}