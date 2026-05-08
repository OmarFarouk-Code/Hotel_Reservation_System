package hotel.GUI.controllers;

import hotel.GUI.utils.SessionManager;
import hotel.model.users.User;
import hotel.model.entities.Room;
import hotel.core.BookingEngine;
import hotel.core.Database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookRoomController {

    @FXML private DatePicker checkInPicker;
    @FXML private DatePicker checkOutPicker;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private ComboBox<String> viewCombo;
    @FXML private Slider priceSlider;
    @FXML private Label priceValueLabel;
    @FXML private Label adultCountLabel;
    @FXML private VBox resultsVBox;

    @FXML private SideBarController sideBarController;
    @FXML private TopBarController topBarController;

    private int adults = 1;

   @FXML
    public void initialize() {
        if (sideBarController != null) {
            User activeUser = SessionManager.getLoggedInUser();
            if (activeUser != null) {
                sideBarController.setRole(activeUser.getTypeofuser().name());
            } else {
                sideBarController.setRole("GUEST");
            }
            sideBarController.setActiveSection("new-booking"); 
        }

        if (topBarController != null) {
            topBarController.setPageTitle("Reservations", "New Booking");
            topBarController.refresh();
        }

        Database.getRoomTypes().forEach(type -> roomTypeCombo.getItems().add(type.getTypeName()));
        viewCombo.getItems().addAll("SEA_VIEW", "GARDEN_VIEW", "CITY_VIEW", "POOL");
        checkInPicker.setValue(LocalDate.now());
        checkOutPicker.setValue(LocalDate.now().plusDays(1));
        adultCountLabel.setText(String.valueOf(adults));
        
        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            priceValueLabel.setText("$" + String.format("%.0f", newVal.doubleValue()));
        });
        priceValueLabel.setText("$" + String.format("%.0f", priceSlider.getValue()));
        
        onSearchRooms();
    }

    @FXML
    void onSearchRooms() {
        resultsVBox.getChildren().clear();

        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();
        List<Room> availableRooms = BookingEngine.getAvailableRooms(checkIn, checkOut);

        int matchCount = 0;

        for (Room room : availableRooms) {
            boolean matchesType = true;
            if (roomTypeCombo.getValue() != null && !roomTypeCombo.getValue().isEmpty() && !roomTypeCombo.getValue().equals("Select Type")) {
                matchesType = room.getRoomType().getTypeName().trim().equalsIgnoreCase(roomTypeCombo.getValue().trim());
            }
            boolean matchesPrice = (room.getRoomType().getEffectivePrice() <= priceSlider.getValue());
            boolean matchesView = true;
            if (viewCombo.getValue() != null && !viewCombo.getValue().isEmpty() && !viewCombo.getValue().equals("Select View")) {
                matchesView = room.getRoomType().getRoomView().toString().trim().equalsIgnoreCase(viewCombo.getValue().trim());
            }

            if (matchesType && matchesPrice && matchesView) {
                loadRoomCard(room);
                matchCount++;
            }
        }
        if (matchCount == 0) {
            Label noResultsLabel = new Label("No rooms found matching your search. \nTry a different date or price range.");
            noResultsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #777777; -fx-padding: 50;");
            noResultsLabel.setWrapText(true);
            noResultsLabel.setAlignment(Pos.CENTER);
            resultsVBox.setAlignment(Pos.CENTER);
            resultsVBox.getChildren().add(noResultsLabel);
        } else {
            resultsVBox.setAlignment(Pos.TOP_LEFT);
        }

        System.out.println("Search completed. Found " + matchCount + " rooms matching your criteria.");
    }
    private void loadRoomCard(Room room) {
        try {
            System.out.println("Displaying card for Room: " + room.getRoomNumber());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hotel/GUI/screens/RoomCard.fxml"));
            HBox cardNode = loader.load();
            cardNode.setMinWidth(600);
            RoomViewController itemController = loader.getController();
            itemController.setRoomData(room);
            itemController.setBookAction(() -> handleBookingConfirmation(room));

            resultsVBox.getChildren().add(cardNode);

        } catch (IOException e) {
            System.err.println("Problem in loading the card: " + e.getMessage());
        }
    }

    private void handleBookingConfirmation(Room room) {
        User loggedInUser = SessionManager.getLoggedInUser();

        if (!(loggedInUser instanceof hotel.model.users.Guest)) {
            showError("Booking Failed", "Only registered guests can make a booking.");
            return;
        }

        hotel.model.users.Guest guest = (hotel.model.users.Guest) loggedInUser;
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        // 1. Create the Confirmation Popup
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Booking");
        confirm.setHeaderText("Complete Your Reservation");
        confirm.setContentText("Are you sure you want to book " + room.getRoomType().getTypeName() +
                " (Room " + room.getRoomNumber() + ") \nFrom: " + checkIn + "\nTo: " + checkOut + "?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int newResId = Database.getReservations().size() + 1;

            // Create the new reservation with all 8 required parameters in the correct order
            hotel.model.bookings.Reservation newReservation = new hotel.model.bookings.Reservation(
                    newResId,
                    guest,
                    room,
                    checkIn,
                    checkOut,
                    null, // Default no dining package selected, handled later or empty for now
                    0,
                    adults
            );

            // Set as pending since the payment isn't complete yet
            newReservation.setStatus(hotel.model.enums.ReservationStatus.PENDING);
            
            // Add to database
            Database.getReservations().add(newReservation);
            Database.saveData();

            // Set the reservation in the CheckoutController and navigate to Checkout screen
            CheckoutController.setPendingReservation(newReservation);
            hotel.GUI.utils.SceneManager.navigate("Checkout.fxml");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void onIncrementAdults() {
        adults++;
        adultCountLabel.setText(String.valueOf(adults));
    }

    @FXML
    void onDecrementAdults() {
        if (adults > 1) {
            adults--;
            adultCountLabel.setText(String.valueOf(adults));
        }
    }
}