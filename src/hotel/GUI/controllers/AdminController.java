package hotel.GUI.controllers;

import hotel.core.BookingEngine;
import hotel.core.Database;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.PromoCode;
import hotel.model.entities.Amenity;
import hotel.model.entities.Room;
import hotel.model.entities.RoomType;
import hotel.model.enums.RoomView;
import hotel.model.staff.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.util.List;

public class AdminController {

    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private VBox invoiceList;

    @FXML private Label totalRevenueLabel;
    @FXML private Label occupancyLabel;
    @FXML private Label activeReservationsLabel;
    @FXML private Label registeredGuestsLabel;

    private BookingEngine engine = new BookingEngine(); 
    private Admin adminSession = new Admin(); // Wrapper to use existing backend functions

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        // 1. Populate Top Metrics
        double totalRev = engine.calculateTotalRevenue(); 
        totalRevenueLabel.setText(String.format("$%.2f", totalRev));

        double occupancy = engine.calculateOccupancyPercentage();
        occupancyLabel.setText(String.format("%.0f%%", occupancy));

        int activeCount = (Database.getReservations() != null) ? Database.getReservations().size() : 0;
        activeReservationsLabel.setText(String.valueOf(activeCount));

        int guestCount = (Database.getGuests() != null) ? Database.getGuests().size() : 0;
        registeredGuestsLabel.setText(String.valueOf(guestCount));

        // 2. Populate Line Chart
        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Last 30 Days");
        
        LocalDate today = LocalDate.now();
        
        try {
            // Note: Fixed to use the instance method 'calcualteTotalRevenue' which throws Exception
            double week1Rev = engine.calcualteTotalRevenue(today.minusDays(7), today);
            double week2Rev = engine.calcualteTotalRevenue(today.minusDays(14), today.minusDays(7));
            double week3Rev = engine.calcualteTotalRevenue(today.minusDays(21), today.minusDays(14));
            double week4Rev = engine.calcualteTotalRevenue(today.minusDays(28), today.minusDays(21));

            series.getData().add(new XYChart.Data<>("Week 4", week4Rev));
            series.getData().add(new XYChart.Data<>("Week 3", week3Rev));
            series.getData().add(new XYChart.Data<>("Week 2", week2Rev));
            series.getData().add(new XYChart.Data<>("Week 1", week1Rev));
        } catch (Exception e) {
            System.err.println("Error loading chart data: " + e.getMessage());
        }
        revenueChart.getData().add(series);

        // 3. Populate Recent Invoices (Take the last 5)
        invoiceList.getChildren().clear();
        List<Invoice> invoices = Database.getInvoices();
        int count = 0;
        for (int i = invoices.size() - 1; i >= 0 && count < 5; i--) {
            Invoice inv = invoices.get(i);
            String statusText = inv.isPaid() ? "Paid" : "Unpaid";
            Label invLabel = new Label(String.format("Invoice #%d | $%.2f | %s", inv.getInvoiceID(), inv.getTotalAmount(), statusText));
            invLabel.setStyle("-fx-font-size: 14px; -fx-padding: 4px;");
            invoiceList.getChildren().add(invLabel);
            count++;
        }
    }

    // ==========================================
    // ACTION BUTTONS (Using JavaFX Dialogs)
    // ==========================================

    @FXML
    public void onAddRoom(ActionEvent event) {
        Dialog<Room> dialog = createBaseDialog("Add Room", "Register a new physical room");
        GridPane grid = setupGrid();

        TextField numberField = new TextField(); numberField.setPromptText("Room Number (e.g. 101)");
        TextField floorField = new TextField(); floorField.setPromptText("Floor (e.g. 1)");
        ComboBox<String> typeBox = new ComboBox<>();
        Database.getRoomTypes().forEach(t -> typeBox.getItems().add(t.getTypeName()));

        grid.add(new Label("Number:"), 0, 0); grid.add(numberField, 1, 0);
        grid.add(new Label("Floor:"), 0, 1);  grid.add(floorField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);   grid.add(typeBox, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(b -> {
            if (b.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    RoomType selectedType = Database.getRoomTypes().stream()
                            .filter(t -> t.getTypeName().equals(typeBox.getValue()))
                            .findFirst().orElseThrow(() -> new Exception("Select a Room Type"));
                    return new Room(Integer.parseInt(numberField.getText()), Integer.parseInt(floorField.getText()), selectedType);
                } catch (Exception e) {
                    showError("Invalid input: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(room -> {
            try {
                adminSession.createRoom(room); // Saves to Database automatically
                refreshDashboard();
                showSuccess("Room created successfully!");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    @FXML
    public void onAddType(ActionEvent event) {
        Dialog<RoomType> dialog = createBaseDialog("Add Room Type", "Create a new category of rooms");
        GridPane grid = setupGrid();

        TextField nameField = new TextField(); nameField.setPromptText("e.g. Presidential Suite");
        TextField priceField = new TextField(); priceField.setPromptText("Base Price");
        ComboBox<RoomView> viewBox = new ComboBox<>(); viewBox.getItems().addAll(RoomView.values());
        TextField descField = new TextField(); descField.setPromptText("Short description");
        TextField capacityField = new TextField(); capacityField.setPromptText("Max Capacity");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1); grid.add(priceField, 1, 1);
        grid.add(new Label("View:"), 0, 2); grid.add(viewBox, 1, 2);
        grid.add(new Label("Desc:"), 0, 3); grid.add(descField, 1, 3);
        grid.add(new Label("Capacity:"), 0, 4); grid.add(capacityField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(b -> {
            if (b.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    double price = Double.parseDouble(priceField.getText());
                    int capacity = Integer.parseInt(capacityField.getText());
                    // RoomType(String typeName, double pricePerNight, RoomView roomView, String description, double seasonMultiplier, double effectivePrice, int maxCapacity)
                    return new RoomType(nameField.getText(), price, viewBox.getValue(), descField.getText(), 1.0, price, capacity);
                } catch (Exception e) {
                    showError("Check your numbers and selections.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(type -> {
            try {
                adminSession.createRoomType(type);
                showSuccess("Room Type added!");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    @FXML
    public void onAddAmenity(ActionEvent event) {
        Dialog<Amenity> dialog = createBaseDialog("Add Amenity", "Create a new Amenity");
        GridPane grid = setupGrid();

        TextField nameField = new TextField(); nameField.setPromptText("Name");
        TextField descField = new TextField(); descField.setPromptText("Description");
        TextField priceField = new TextField(); priceField.setPromptText("Price");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Desc:"), 0, 1); grid.add(descField, 1, 1);
        grid.add(new Label("Price:"), 0, 2); grid.add(priceField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(b -> {
            if (b.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    return new Amenity(nameField.getText(), descField.getText(), Double.parseDouble(priceField.getText()));
                } catch (Exception e) {
                    showError("Ensure price is a valid number.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(amenity -> {
            try {
                adminSession.createAmenity(amenity);
                showSuccess("Amenity Added!");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    @FXML
    public void onCreatePromo(ActionEvent event) {
        Dialog<PromoCode> dialog = createBaseDialog("Create Promo", "Generate a new Discount Code");
        GridPane grid = setupGrid();

        TextField codeField = new TextField(); codeField.setPromptText("e.g. SUMMER20");
        TextField discountField = new TextField(); discountField.setPromptText("e.g. 0.20 for 20%");
        TextField daysField = new TextField(); daysField.setPromptText("Days until expiry");

        grid.add(new Label("Code:"), 0, 0); grid.add(codeField, 1, 0);
        grid.add(new Label("Discount:"), 0, 1); grid.add(discountField, 1, 1);
        grid.add(new Label("Valid Days:"), 0, 2); grid.add(daysField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(b -> {
            if (b.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    double discount = Double.parseDouble(discountField.getText());
                    int days = Integer.parseInt(daysField.getText());
                    return new PromoCode(codeField.getText().toUpperCase(), discount, LocalDate.now().plusDays(days));
                } catch (Exception e) {
                    showError("Invalid numeric data.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(promo -> {
            Database.getPromoCodes().add(promo);
            Database.saveData(); // Save manually since Admin class lacks a promo wrapper
            showSuccess("Promo Code activated!");
        });
    }

    // ==========================================
    // UI UTILITIES
    // ==========================================

    private <T> Dialog<T> createBaseDialog(String title, String header) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Save", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
        return dialog;
    }

    private GridPane setupGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));
        return grid;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.show();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.show();
    }
}