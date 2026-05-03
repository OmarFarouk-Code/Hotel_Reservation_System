package hotel.GUI.controllers;

import hotel.GUI.utils.SessionManager;
import hotel.core.BookingEngine;
import hotel.model.bookings.Reservation;
import hotel.model.users.Guest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class GuestDashboard {

    // Nested Controllers injected by JavaFX <fx:include>
    @FXML private SideBarController sideBarController;
    @FXML private TopBarController topBarController;

    // Dashboard UI Elements
    @FXML private Label lblGreeting;
    @FXML private Label lblActiveReservations;
    @FXML private Label lblBalance;
    @FXML private Label lblNextCheckIn;
    
    // Latest Reservation Card Elements
    @FXML private VBox latestResBox;
    @FXML private Label lblRoomType;
    @FXML private Label lblCheckInDate;
    @FXML private Label lblCheckOutDate;

    private BookingEngine engine;

    @FXML
    public void initialize() {
        engine = new BookingEngine();

        // 1. Initialize the embedded Side Bar
        if (sideBarController != null) {
            sideBarController.setRole("GUEST");
            sideBarController.setActiveSection("concierge");
        }

        // 2. Initialize the embedded Top Bar
        if (topBarController != null) {
            topBarController.setPageTitle("The Digital Concierge", "Dashboard");
            topBarController.refresh(); // Tells top bar to grab username from SessionManager
        }

        // 3. Load Logged-In User Data
        Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
        if (activeGuest != null) {
            populateDashboard(activeGuest);
        }
    }

    private void populateDashboard(Guest guest) {
        // Set basic details
        lblGreeting.setText("Good Morning, " + guest.getUserName());
        lblBalance.setText(String.format("%,.2f", guest.getBalance()));

        // Fetch user's reservations
        List<Reservation> guestRes = engine.getReservationsForGuest(guest);
        lblActiveReservations.setText(String.valueOf(guestRes.size()));

        // Populate Latest Reservation Card
        if (!guestRes.isEmpty()) {
            // Get the most recent booking
            Reservation latest = guestRes.get(guestRes.size() - 1);
            
            lblRoomType.setText(latest.getRoom().getRoomType().getTypeName());
            lblCheckInDate.setText(latest.getCheckinDate().toString());
            lblCheckOutDate.setText(latest.getCheckoutDate().toString());
            lblNextCheckIn.setText(latest.getCheckinDate().toString());
            
            latestResBox.setDisable(false);
        } else {
            // State when user has no reservations
            lblRoomType.setText("No Recent Bookings");
            lblCheckInDate.setText("-");
            lblCheckOutDate.setText("-");
            lblNextCheckIn.setText("None");
            latestResBox.setDisable(true); // Grays out the card
        }
    }

    @FXML
    private void onCancelReservation() {
        // TODO: Logic to cancel the reservation or navigate to the Reservations tab
        System.out.println("Cancel Reservation Clicked");
    }
}