// src/main/java/com/example/tp3/TicketExporter.java - À compléter
package com.example.tp3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

// TODO-1 : créer une classe utilitaire finale TicketExporter
// TODO-2 : créer une méthode exportToCsv(Collection<SupportTicket> tickets, String filePath)
// TODO-3 : créer le dossier parent si nécessaire
// TODO-4 : écrire un en-tête CSV
// TODO-5 : écrire chaque ticket sous forme de ligne

public final class TicketExporter {

    public static void exportToCsv(Collection<SupportTicket> tickets, String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        try (var writer = Files.newBufferedWriter(path)) {
            writer.write("id,title,customerName,priority,createdAt,description,urgent,status");
            writer.newLine();
            for (SupportTicket ticket : tickets) {
                writer.write(String.format("%d,%s,%s,%s,%s,%s,%b,%s",
                        ticket.getId(),
                        escapeCsv(ticket.getTitle()),
                        escapeCsv(ticket.getCustomerName()),
                        escapeCsv(ticket.getPriority()),
                        ticket.getCreatedAt(),
                        escapeCsv(ticket.getDescription()),
                        ticket.isUrgent(),
                        escapeCsv(ticket.getStatus())));
                writer.newLine();
            }
        }
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

}