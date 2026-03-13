// src/main/java/com/example/tp3/DatabaseManager.java - À compléter
package com.example.tp3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



public final class DatabaseManager {

    // TODO-1 : définir une constante DB_URL = "jdbc:sqlite:tickets.db"
    // TODO-2 : créer un constructeur privé
    // TODO-3 : créer une méthode public static Connection getConnection() throws SQLException
    // TODO-4 : créer une méthode public static void initializeDatabase()
    // TODO-5 : dans initializeDatabase(), exécuter un CREATE TABLE IF NOT EXISTS support_tickets (...)
    // Colonnes attendues :
    // id INTEGER PRIMARY KEY AUTOINCREMENT
    // title TEXT NOT NULL
    // customer_name TEXT NOT NULL
    // priority TEXT NOT NULL
    // created_at TEXT NOT NULL
    // description TEXT NOT NULL
    // urgent INTEGER NOT NULL
    // status TEXT NOT NULL
    private static final String DB_URL = "jdbc:sqlite:tickets.db";
    
    private DatabaseManager() {
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS support_tickets (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "customer_name TEXT NOT NULL, " +
                    "priority TEXT NOT NULL, " +
                    "created_at TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "urgent INTEGER NOT NULL, " +
                    "status TEXT NOT NULL)");
        }
    }
    

}