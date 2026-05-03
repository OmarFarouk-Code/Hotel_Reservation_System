package hotel.GUI.controllers;

import hotel.core.BookingEngine;
import hotel.model.bookings.Reservation;
import hotel.model.entities.Room;
import hotel.model.users.Guest;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;

import java.time.LocalDate;


import javafx.scene.control.cell.PropertyValueFactory;

// Importing from your specific repo structure


// Importing based on your provided package structure
import hotel.model.enums.RoomView;
import hotel.model.enums.DiningPackage;

import java.util.List;

public class GuestDashboard {

    // --- FXML IDs (Match these in Scene Builder) ---
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, Integer> colRoomNum;
    @FXML private TableColumn<Room, String> colType;
    @FXML private TableColumn<Room, Double> colPrice;
    @FXML private Label lblActiveReservations; // This MUST match the fx:id in Scene Builder exactly.
    @FXML private DatePicker dpCheckIn;
    @FXML private DatePicker dpCheckOut;
    @FXML private ComboBox<RoomView> comboView;
    @FXML private ComboBox<DiningPackage> comboDining;
    @FXML private TextField txtPromo;
    @FXML private Label lblTotal;

    private BookingEngine engine;
    private Guest activeGuest; // This would be set during login

    @FXML
    public void initialize() {
        engine = new BookingEngine();

        // Mapping Table Columns to your Room entity getters
        colRoomNum.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType")); // JavaFX calls getRoomType()
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));

        // Populating dropdowns from your Enums
        comboView.setItems(FXCollections.observableArrayList(RoomView.values()));
        comboDining.setItems(FXCollections.observableArrayList(DiningPackage.values()));
    }

    /**
     * Logic for the 'Search' button using your getAvailableRooms method.
     */
    @FXML
    private void onSearchClicked() {
        LocalDate start = dpCheckIn.getValue();
        LocalDate end = dpCheckOut.getValue();

        if (start != null && end != null) {
            try {
                // Using your static method from BookingEngine
                List<Room> available = BookingEngine.getAvailableRooms(start, end);

                // Filtering further if a specific view is selected
                if (comboView.getValue() != null) {
                    available.removeIf(r -> !r.getRoomType().getRoomView().equals(comboView.getValue()));
                }

                roomTable.setItems(FXCollections.observableArrayList(available));
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Dates", e.getMessage());
            }
        }
    }

    /**
     * Logic for the 'Book' button using your createDraftReservation method.
     */
    @FXML
    private void onBookClicked() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();

        if (selected == null || dpCheckIn.getValue() == null || comboDining.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select a room, dates, and dining package.");
            return;
        }

        try {
            // Calculates cost using your calculateRoomCost logic
            double cost = engine.calculateRoomCost(selected, dpCheckIn.getValue(), dpCheckOut.getValue());
            double dining = engine.calculateDiningCost(comboDining.getValue(), 1); // Sample for 1 night

            // Apply promo multiplier from your validatePromocode method
            double discount = engine.validatePromocode(txtPromo.getText());
            double total = (cost + dining) * discount;

            lblTotal.setText("Total: EGP " + String.format("%.2f", total));

            // Calls your draft reservation logic
            engine.createDraftReservation(activeGuest, selected, dpCheckIn.getValue(),
                    dpCheckOut.getValue(), comboDining.getValue(), 0, 1);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Draft reservation created! Please pay your invoice.");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Booking Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to set the guest from the Login screen
    public void setGuest(Guest guest) {
        this.activeGuest = guest;
    }
    // Add this variable at the top with your other @FXML fields


    // Add this logic inside your initialize() or a refresh method
    public void updateDashboardStats(Guest guest) {
        // 1. Get the list of reservations for the logged-in guest
        // Using the method from your BookingEngine.java
        List<Reservation> guestRes = engine.getReservationsForGuest(guest);

        // 2. Set the text of the Label to the size of that list
        lblActiveReservations.setText(String.valueOf(guestRes.size()));
    }
}