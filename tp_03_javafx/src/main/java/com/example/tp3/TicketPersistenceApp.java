package com.example.tp3;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class TicketPersistenceApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.initializeDatabase();
        TicketPersistenceService service = new TicketPersistenceService();

        TextField titleField = new TextField();
        TextField customerField = new TextField();
        TextField searchField = new TextField();
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "High");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Open", "In Progress", "Closed");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextArea descriptionArea = new TextArea();
        CheckBox urgentCheck = new CheckBox("Urgent");
        Label statusLabel = new Label();

        TableView<SupportTicket> tableView = new TableView<>();
        TableColumn<SupportTicket, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getId()).asObject());
        TableColumn<SupportTicket, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        TableColumn<SupportTicket, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerName()));
        TableColumn<SupportTicket, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriority()));
        TableColumn<SupportTicket, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt().toString()));
        TableColumn<SupportTicket, Boolean> urgentCol = new TableColumn<>("Urgent");
        urgentCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isUrgent()).asObject());
        TableColumn<SupportTicket, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        tableView.getColumns().addAll(idCol, titleCol, customerCol, priorityCol, dateCol, urgentCol, statusCol);
        tableView.setItems(service.getTickets());

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button resetButton = new Button("Reset");
        Button reloadButton = new Button("Reload");
        Button exportButton = new Button("Export CSV");

        addButton.setOnAction(event -> {
            String title = titleField.getText().trim();
            String customer = customerField.getText().trim();
            String priority = priorityBox.getValue();
            LocalDate date = datePicker.getValue();
            String description = descriptionArea.getText().trim();
            boolean urgent = urgentCheck.isSelected();
            String status = statusBox.getValue();
            if (!title.isEmpty() && !customer.isEmpty() && priority != null && status != null) {
                SupportTicket newTicket = new SupportTicket(title, customer, priority, date, description, urgent, status);
                service.createTicket(newTicket);
                titleField.clear();
                customerField.clear();
                priorityBox.getSelectionModel().clearSelection();
                statusBox.getSelectionModel().clearSelection();
                datePicker.setValue(LocalDate.now());
                descriptionArea.clear();
                urgentCheck.setSelected(false);
            } else {
                statusLabel.setText("Please fill in all required fields.");
            }
        });

        updateButton.setOnAction(event -> {
            SupportTicket selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String title = titleField.getText().trim();
                String customer = customerField.getText().trim();
                String priority = priorityBox.getValue();
                LocalDate date = datePicker.getValue();
                String description = descriptionArea.getText().trim();
                boolean urgent = urgentCheck.isSelected();
                String status = statusBox.getValue();
                if (!title.isEmpty() && !customer.isEmpty() && priority != null && status != null) {
                    SupportTicket updated = new SupportTicket(selected.getId(), title, customer, priority, date, description, urgent, status);
                    service.updateTicket(updated);
                } else {
                    statusLabel.setText("Please fill in all required fields.");
                }
            } else {
                statusLabel.setText("Please select a ticket to update.");
            }
        });

        deleteButton.setOnAction(event -> {
            SupportTicket selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.deleteTicket(selected.getId());
            } else {
                statusLabel.setText("Please select a ticket to delete.");
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                titleField.setText(newSelection.getTitle());
                customerField.setText(newSelection.getCustomerName());
                priorityBox.setValue(newSelection.getPriority());
                datePicker.setValue(newSelection.getCreatedAt());
                descriptionArea.setText(newSelection.getDescription());
                urgentCheck.setSelected(newSelection.isUrgent());
                statusBox.setValue(newSelection.getStatus());
            }
        });

        reloadButton.setOnAction(event -> service.refresh());

        resetButton.setOnAction(event -> {
            titleField.clear();
            customerField.clear();
            priorityBox.getSelectionModel().clearSelection();
            statusBox.getSelectionModel().clearSelection();
            datePicker.setValue(LocalDate.now());
            descriptionArea.clear();
            urgentCheck.setSelected(false);
            tableView.getSelectionModel().clearSelection();
        });

        exportButton.setOnAction(event -> {
            try {
                TicketExporter.exportToCsv(service.getTickets(), "export/tickets_export.csv");
                statusLabel.setText("Exported to tickets_export.csv");
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                tableView.setItems(service.getTickets());
            } else {
                tableView.setItems(javafx.collections.FXCollections.observableArrayList(service.search(newText)));
            }
        });

        // Layout
        HBox formRow1 = new HBox(10, new Label("Title:"), titleField, new Label("Customer:"), customerField);
        HBox formRow2 = new HBox(10, new Label("Priority:"), priorityBox, new Label("Status:"), statusBox);
        HBox formRow3 = new HBox(10, new Label("Date:"), datePicker, urgentCheck);
        HBox buttonRow = new HBox(10, addButton, updateButton, deleteButton, resetButton, reloadButton, exportButton);
        VBox formBox = new VBox(10, formRow1, formRow2, formRow3, descriptionArea, buttonRow, statusLabel);
        formBox.setPadding(new Insets(10));
        BorderPane root = new BorderPane();
        root.setLeft(formBox);
        root.setCenter(tableView);
        root.setTop(searchField);
        BorderPane.setMargin(searchField, new Insets(10));

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/com/example/tp3/ticket-persistence.css").toExternalForm());
        primaryStage.setTitle("TP 03 - Ticket Persistence App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}




