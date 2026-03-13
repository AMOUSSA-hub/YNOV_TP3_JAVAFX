// src/main/java/com/example/tp3/TicketPersistenceServiceTests.java - À compléter
package com.example.tp3;

import java.time.LocalDate;

public class TicketPersistenceServiceTests {

    public static void main(String[] args) {
        try {
            // TODO-1 : initialiser la base
            System.out.println("=== Initializing database ===");
            DatabaseManager.initializeDatabase();
            System.out.println("Database initialized successfully\n");
            
            // TODO-2 : créer un TicketPersistenceService
            System.out.println("=== Creating TicketPersistenceService ===");
            TicketPersistenceService service = new TicketPersistenceService();
            System.out.println("Service created successfully\n");
            
            // TODO-3 : vider la table
            System.out.println("=== Clearing all tickets ===");
            service.deleteAllTickets();
            System.out.println("All tickets deleted\n");
            
            // TODO-4 : créer deux tickets
            System.out.println("=== Creating two tickets ===");
            SupportTicket ticket1 = new SupportTicket(
                    "Cannot login",
                    "John Doe",
                    "HIGH",
                    LocalDate.now(),
                    "User cannot access the application",
                    true,
                    "OPEN"
            );
            
            SupportTicket ticket2 = new SupportTicket(
                    "Database connection issue",
                    "Jane Smith",
                    "MEDIUM",
                    LocalDate.now(),
                    "Cannot connect to the database server",
                    false,
                    "IN_PROGRESS"
            );
            
            SupportTicket created1 = service.createTicket(ticket1);
            SupportTicket created2 = service.createTicket(ticket2);
            System.out.println("Ticket 1 created: " + created1);
            System.out.println("Ticket 2 created: " + created2 + "\n");
            
            // TODO-5 : vérifier le contenu de l'ObservableList
            System.out.println("=== Verifying ObservableList content ===");
            System.out.println("Number of tickets: " + service.getTickets().size());
            for (SupportTicket ticket : service.getTickets()) {
                System.out.println("  - " + ticket);
            }
            System.out.println();
            
            // TODO-6 : tester search("mot-clé")
            System.out.println("=== Testing search functionality ===");
            var searchResults = service.search("login");
            System.out.println("Search results for 'login': " + searchResults.size() + " ticket(s)");
            for (SupportTicket ticket : searchResults) {
                System.out.println("  - " + ticket);
            }
            System.out.println();
            
            // TODO-7 : tester deleteAllTickets()
            System.out.println("=== Testing deleteAllTickets ===");
            service.deleteAllTickets();
            System.out.println("All tickets deleted");
            System.out.println("Number of remaining tickets: " + service.getTickets().size());
            
            System.out.println("\n=== All tests completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Error during tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}