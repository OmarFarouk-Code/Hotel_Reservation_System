package hotel.GUI.controllers;

import hotel.GUI.utils.SessionManager;
import hotel.core.BookingEngine;
import hotel.model.bookings.Reservation;
import hotel.model.users.Guest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.List;

public class GuestDashboard {

    // Nested Controllers injected by JavaFX <fx:include>
    @FXML private SideBarController sideBarController;
    @FXML private TopBarController topBarController;

    // Stats row labels
    @FXML private Label lblGreeting;
    @FXML private Label lblActiveReservations;
    @FXML private Label lblBalance;
    @FXML private Label lblNextCheckIn;

    // Latest Reservation card — HBox after layout fix
    @FXML private HBox latestResBox;
    @FXML private Label lblRoomType;
    @FXML private Label lblCheckInDate;
    @FXML private Label lblCheckOutDate;

    // Room image on the right side of the card
    @FXML private ImageView roomImageView;

    private BookingEngine engine;
    private Reservation latestReservation;

    private static final String FALLBACK_IMAGE = "/hotel/GUI/assets/room-default.jpg";
    @FXML
    public void initialize() {
        engine = new BookingEngine();

        // 1. Configure the embedded Side Bar
        if (sideBarController != null) {
            sideBarController.setRole("GUEST");
            sideBarController.setActiveSection("concierge");
        }

        // 2. Configure the embedded Top Bar
        if (topBarController != null) {
            topBarController.setPageTitle("The Digital Concierge", "Dashboard");
            topBarController.refresh();
        }

        // 3. Load the logged-in guest's data
        Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
        if (activeGuest != null) {
            populateDashboard(activeGuest);
        }
    }

    // -------------------------------------------------------------------------
    // Data population
    // -------------------------------------------------------------------------

    private void populateDashboard(Guest guest) {
        // Greeting & balance
        lblGreeting.setText("Good Morning, " + guest.getUserName());
        lblBalance.setText(String.format("%,.2f", guest.getBalance()));

        // Active/pending reservations
        List<Reservation> guestRes = engine.getReservationsForGuest(guest);
        lblActiveReservations.setText(String.valueOf(guestRes.size()));

        if (!guestRes.isEmpty()) {
            latestReservation = guestRes.get(guestRes.size() - 1);

            lblRoomType.setText(latestReservation.getRoom().getRoomType().getTypeName());
            lblCheckInDate.setText(latestReservation.getCheckinDate().toString());
            lblCheckOutDate.setText(latestReservation.getCheckoutDate().toString());
            lblNextCheckIn.setText(latestReservation.getCheckinDate().toString());

            loadFallbackImage();

            latestResBox.setDisable(false);
            latestResBox.setOpacity(1.0);

        } else {
            latestReservation = null;

            lblRoomType.setText("No Recent Bookings");
            lblCheckInDate.setText("-");
            lblCheckOutDate.setText("-");
            lblNextCheckIn.setText("None");

            loadFallbackImage();

            latestResBox.setDisable(true);
            latestResBox.setOpacity(0.5);
        }
    }

    private void loadFallbackImage() {
        if (roomImageView == null) return;
        try {
            Image fallback = new Image(new java.io.File(FALLBACK_IMAGE).toURI().toString());
            roomImageView.setImage(fallback);
        } catch (Exception e) {
            System.err.println("GuestDashboard: fallback image missing — " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // FXML action handlers
    // -------------------------------------------------------------------------

    @FXML
    private void onCancelReservation() {
        if (latestReservation == null) return;

        engine.processCancellation(
                latestReservation.getReservationID(),
                java.time.LocalDate.now()
        );

        Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
        if (activeGuest != null) {
            populateDashboard(activeGuest);
        }
    }

    @FXML
    private void onViewInvoice() {
        if (latestReservation == null) return;

        hotel.model.bookings.Invoice invoice =
                engine.generateInvoice(latestReservation, null);
        System.out.println("Invoice for reservation #"
                + latestReservation.getReservationID() + ": " + invoice);
    }

    @FXML
    private void onModifyStay() {
        // TODO: Navigate to the booking / modification screen
        System.out.println("Modify Stay clicked");
    }
}