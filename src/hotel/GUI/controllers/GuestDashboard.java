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

    private static final String ROOMS_ASSET_PATH = "/hotel/GUI/assets/rooms/";
    private static final String DEFAULT_IMAGE     = ROOMS_ASSET_PATH + "default.jpg";
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

            loadRoomImage(latestReservation.getRoom().getRoomType().getTypeName());

            latestResBox.setDisable(false);
            latestResBox.setOpacity(1.0);

        } else {
            latestReservation = null;

            lblRoomType.setText("No Recent Bookings");
            lblCheckInDate.setText("-");
            lblCheckOutDate.setText("-");
            lblNextCheckIn.setText("None");

            loadRoomImage(null);

            latestResBox.setDisable(true);
            latestResBox.setOpacity(0.5);
        }
    }

    /**
     * Loads the image that matches the given room-type name from
     * {@code /hotel/GUI/assets/rooms/<typeName>.jpg}.
     * Falls back to {@code default.jpg} when the type-specific image is missing,
     * and silently skips when {@code roomImageView} is null or {@code typeName}
     * is null/blank (e.g. when there are no reservations).
     *
     * @param typeName the exact type name returned by {@code RoomType.getTypeName()},
     *                 or {@code null} to load the default image directly.
     */
    private void loadRoomImage(String typeName) {
        if (roomImageView == null) return;

        try {
            java.io.InputStream imageStream = null;

            // 1. Try the room-type specific image
            if (typeName != null && !typeName.isBlank()) {
                String imagePath = ROOMS_ASSET_PATH + typeName + ".jpg";
                imageStream = getClass().getResourceAsStream(imagePath);
                if (imageStream == null) {
                    System.out.println("GuestDashboard: no image found for type \"" + typeName + "\", using default.");
                }
            }

            // 2. Fall back to default.jpg
            if (imageStream == null) {
                imageStream = getClass().getResourceAsStream(DEFAULT_IMAGE);
            }

            if (imageStream != null) {
                roomImageView.setImage(new Image(imageStream));
            } else {
                System.err.println("GuestDashboard: default room image also missing — check assets/rooms/default.jpg");
            }

        } catch (Exception e) {
            System.err.println("GuestDashboard: failed to load room image — " + e.getMessage());
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