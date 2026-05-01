package hotel.GUI.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * JavaFX Controller for the Guest Dashboard screen.
 *
 * Linked to: Guest_Dashboard.fxml  (Scene Builder)
 *
 * All @FXML field IDs must match exactly what is set
 * in Scene Builder's "Code" tab for each node.
 */
public class Guest_Dashboard implements Initializable {

    // ─── Date / Time Formatters ────────────────────────────────────────────────

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");

    // ─── @FXML Nodes ──────────────────────────────────────────────────────────
    // IDs must match Scene Builder exactly.

    // Header
    @FXML private Label welcomeLabel;             // "Good Morning, Alice."
    @FXML private Label personalizedNoteLabel;    // room + preference sub-text

    // Stat Cards
    @FXML private Label activeReservationsLabel;    // "1"
    @FXML private Label activeReservationSubLabel;  // "Deluxe Suite booked"
    @FXML private Label balanceLabel;               // "$3,200.00"
    @FXML private Label balanceSubLabel;            // "Total settled"
    @FXML private Label upcomingCheckInLabel;       // "Apr 22, 2026"
    @FXML private Label checkInCountdownLabel;      // "In 14 days"

    // Latest Reservation Card
    @FXML private Label roomTypeLabel;              // "Deluxe Suite"
    @FXML private Label reservationStatusLabel;     // "CONFIRMED"
    @FXML private Label reservationSubtitleLabel;   // "Room 204 • Grand Heritage Hotel & Spa"
    @FXML private Label reservationTotalLabel;      // "$4,500.00"
    @FXML private Label checkInDateLabel;           // "Apr 22, 2026"
    @FXML private Label checkInTimeLabel;           // "3:00 PM"
    @FXML private Label checkOutDateLabel;          // "Apr 27, 2026"
    @FXML private Label checkOutTimeLabel;          // "11:00 AM"
    @FXML private ImageView roomImageView;          // Room gallery photo

    // ─── Session Data ─────────────────────────────────────────────────────────
    // Placeholder values match the screenshot exactly.
    // In production, replace these by calling setSessionData() from your
    // login controller right after FXMLLoader.load() (see bottom of file).

    private String    guestFirstName    = "Alice";
    private String    roomPreference    = "feather pillows";

    private int       activeResCount    = 1;
    private String    activeResType     = "Deluxe Suite";

    private double    accountBalance    = 3_200.00;
    private double    reservationTotal  = 4_500.00;

    private String    roomNumber        = "204";
    private String    reservationStatus = "CONFIRMED"; // CONFIRMED | PENDING | CANCELLED

    private LocalDate checkInDate       = LocalDate.of(2026, 4, 22);
    private LocalTime checkInTime       = LocalTime.of(15, 0);   // 3:00 PM
    private LocalDate checkOutDate      = LocalDate.of(2026, 4, 27);
    private LocalTime checkOutTime      = LocalTime.of(11, 0);   // 11:00 AM

    // ─── Initializable ────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateHeader();
        populateStatCards();
        populateLatestReservation();
        loadRoomImage();
    }

    // ─── Population Methods ───────────────────────────────────────────────────

    /**
     * Sets the greeting and the personalized sub-note in the header banner.
     */
    private void populateHeader() {
        welcomeLabel.setText(buildGreeting(guestFirstName));
        personalizedNoteLabel.setText(buildPersonalizedNote());
    }

    /**
     * Populates all three stat cards:
     *   Active Reservations | Account Balance | Upcoming Check-In
     */
    private void populateStatCards() {

        // Card 1 – Active Reservations
        activeReservationsLabel.setText(String.valueOf(activeResCount));
        activeReservationSubLabel.setText(
                activeResCount > 0 ? activeResType + " booked" : "No active reservations");

        // Card 2 – Account Balance
        balanceLabel.setText(formatMoney(accountBalance));
        balanceSubLabel.setText(buildBalanceLabel());

        // Card 3 – Upcoming Check-In
        upcomingCheckInLabel.setText(checkInDate.format(DATE_FMT));
        checkInCountdownLabel.setText(buildCountdownLabel());
    }

    /**
     * Populates every field in the Latest Reservation card.
     */
    private void populateLatestReservation() {
        roomTypeLabel.setText(activeResType);
        reservationStatusLabel.setText(reservationStatus);
        reservationSubtitleLabel.setText(
                "Room " + roomNumber + " \u2022 Grand Heritage Hotel & Spa");
        reservationTotalLabel.setText(formatMoney(reservationTotal));

        checkInDateLabel.setText(checkInDate.format(DATE_FMT));
        checkInTimeLabel.setText(checkInTime.format(TIME_FMT));
        checkOutDateLabel.setText(checkOutDate.format(DATE_FMT));
        checkOutTimeLabel.setText(checkOutTime.format(TIME_FMT));
    }

    /**
     * Loads the room photo from /resources/images/room.jpg.
     * Fails silently with a console message if the file is missing.
     */
    private void loadRoomImage() {
        try {
            Image image = new Image(
                    getClass().getResourceAsStream("/images/room.jpg"));
            roomImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("[Guest_Dashboard] Room image not found: " + e.getMessage());
        }
    }

    // ─── String Builders ──────────────────────────────────────────────────────

    /**
     * Returns a time-of-day greeting.
     * "Good Morning / Afternoon / Evening, {name}."
     */
    private String buildGreeting(String firstName) {
        int hour = LocalTime.now().getHour();
        String period;
        if      (hour < 12) period = "Good Morning";
        else if (hour < 17) period = "Good Afternoon";
        else                period = "Good Evening";
        return period + ", " + firstName + ".";
    }

    /**
     * Returns the personalized sub-text shown beneath the greeting.
     * Mentions the upcoming room and the guest's recorded preference.
     */
    private String buildPersonalizedNote() {
        return "Your stay at Grand Heritage is approaching. "
                + "We are preparing Room " + roomNumber
                + " for your arrival and have noted your preference for "
                + roomPreference + ".";
    }

    /**
     * Returns an account balance sub-label.
     * "Total settled" when paid in full, "Balance due: $X" otherwise.
     */
    private String buildBalanceLabel() {
        if (accountBalance >= reservationTotal) return "Total settled";
        if (accountBalance == 0)               return "Payment pending";
        return "Balance due: " + formatMoney(reservationTotal - accountBalance);
    }

    /**
     * Returns a human-readable countdown to check-in.
     * "Today" | "Tomorrow" | "In N days" | "Stay completed"
     */
    private String buildCountdownLabel() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
        if (days < 0)  return "Stay completed";
        if (days == 0) return "Today";
        if (days == 1) return "Tomorrow";
        return "In " + days + " days";
    }

    /**
     * Formats a double as a USD currency string.
     * e.g.  4500.0  →  "$4,500.00"
     */
    private String formatMoney(double amount) {
        return String.format("$%,.2f", amount);
    }

    // ─── Button Handlers (@FXML) ──────────────────────────────────────────────

    /**
     * "Modify Stay" button.
     * Guards: reservation must be CONFIRMED and check-in must not have started.
     * On success, opens Modify_Stay.fxml as a modal window.
     */
    @FXML
    private void handleModifyStay() {
        if (!reservationStatus.equalsIgnoreCase("CONFIRMED")) {
            showAlert(AlertType.WARNING, "Cannot Modify",
                    "Only confirmed reservations can be modified.");
            return;
        }
        if (!LocalDate.now().isBefore(checkInDate)) {
            showAlert(AlertType.WARNING, "Cannot Modify",
                    "Modifications are not allowed after check-in has begun.");
            return;
        }
        openWindow("/hotel/GUI/views/Modify_Stay.fxml", "Modify Stay");
    }

    /**
     * "View Invoice" button.
     * Opens the invoice view for the current reservation.
     */
    @FXML
    private void handleViewInvoice() {
        openWindow("/hotel/GUI/views/Invoice.fxml", "Invoice");
    }

    /**
     * "Cancel Reservation" button.
     * Shows a confirmation dialog; updates the status label on confirmation.
     */
    @FXML
    private void handleCancelReservation() {
        if (!reservationStatus.equalsIgnoreCase("CONFIRMED")) {
            showAlert(AlertType.WARNING, "Cannot Cancel",
                    "This reservation is already "
                            + reservationStatus.toLowerCase() + ".");
            return;
        }

        Optional<ButtonType> choice = showConfirmation(
                "Cancel Reservation",
                "Are you sure you want to cancel your " + activeResType + " reservation?\n"
                        + "Cancellation fees may apply per hotel policy.");

        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            // Update local state
            reservationStatus = "CANCELLED";
            activeResCount    = 0;

            // Refresh the UI immediately — no need to reload the whole screen
            reservationStatusLabel.setText("CANCELLED");
            populateStatCards();

            showAlert(AlertType.INFORMATION, "Reservation Cancelled",
                    "Your reservation has been cancelled.\n"
                            + "Any eligible refund will be processed within 5–7 business days.");
        }
    }

    /**
     * Notification bell button in the top navigation bar.
     */
    @FXML
    private void handleNotifications() {
        openWindow("/hotel/GUI/views/Notifications.fxml", "Notifications");
    }

    /**
     * Settings (gear icon) button in the top navigation bar.
     */
    @FXML
    private void handleSettings() {
        openWindow("/hotel/GUI/views/Settings.fxml", "Settings");
    }

    /**
     * Help (? icon) button in the top navigation bar.
     */
    @FXML
    private void handleHelp() {
        openWindow("/hotel/GUI/views/Help.fxml", "Help & Support");
    }

    /**
     * "New Booking" button in the sidebar footer.
     */
    @FXML
    private void handleNewBooking() {
        openWindow("/hotel/GUI/views/New_Booking.fxml", "New Booking");
    }

    // ── Sidebar Navigation Handlers ────────────────────────────────────────────

    /** Sidebar → Concierge (current screen — refreshes data in place) */
    @FXML
    private void handleNavConcierge() {
        populateHeader();
        populateStatCards();
        populateLatestReservation();
    }

    /** Sidebar → Reservations */
    @FXML
    private void handleNavReservations() {
        openWindow("/hotel/GUI/views/Reservations.fxml", "Reservations");
    }

    /** Sidebar → Room Map */
    @FXML
    private void handleNavRoomMap() {
        openWindow("/hotel/GUI/views/Room_Map.fxml", "Room Map");
    }

    /** Sidebar → Guest Profiles */
    @FXML
    private void handleNavGuestProfiles() {
        openWindow("/hotel/GUI/views/Guest_Profiles.fxml", "Guest Profiles");
    }

    /** Sidebar → Analytics */
    @FXML
    private void handleNavAnalytics() {
        openWindow("/hotel/GUI/views/Analytics.fxml", "Analytics");
    }

    // ─── Window / Dialog Helpers ──────────────────────────────────────────────

    /**
     * Opens a new FXML screen as a modal dialog.
     *
     * @param fxmlPath classpath path to the FXML file
     * @param title    window title bar text
     */
    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println(
                    "[Guest_Dashboard] Could not open " + fxmlPath + ": " + e.getMessage());
            showAlert(AlertType.ERROR, "Navigation Error",
                    "Could not open \"" + title + "\". Please try again.");
        }
    }

    /**
     * Shows a simple one-button alert dialog.
     *
     * @param type    AlertType.INFORMATION | WARNING | ERROR
     * @param title   dialog title
     * @param message body message shown to the user
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an OK / Cancel confirmation dialog and returns the user's choice.
     *
     * @param title   dialog title
     * @param message body message shown to the user
     * @return Optional<ButtonType> — check if ButtonType.OK was pressed
     */
    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(null);
        confirm.setContentText(message);
        return confirm.showAndWait();
    }

    // ─── Public Session Injector ──────────────────────────────────────────────

    /**
     * Injects live guest and reservation data from your login controller.
     *
     * Call this AFTER FXMLLoader.load() and BEFORE stage.show() so that
     * initialize() has not yet run with stale placeholder data.
     *
     * Example in your login controller:
     * ─────────────────────────────────────────────────────────────────────────
     *   FXMLLoader loader = new FXMLLoader(
     *           getClass().getResource("/hotel/GUI/views/Guest_Dashboard.fxml"));
     *   Parent root = loader.load();
     *
     *   Guest_Dashboard dashboard = loader.getController();
     *   dashboard.setSessionData(
     *           loggedInGuest.getFirstName(),
     *           loggedInGuest.getRoomPreference(),
     *           reservationList.size(),
     *           latestReservation.getRoomType(),
     *           accountService.getBalance(guestId),
     *           latestReservation.getTotalCost(),
     *           latestReservation.getRoomNumber(),
     *           latestReservation.getStatus(),
     *           latestReservation.getCheckInDate(),
     *           latestReservation.getCheckInTime(),
     *           latestReservation.getCheckOutDate(),
     *           latestReservation.getCheckOutTime()
     *   );
     *
     *   Stage stage = new Stage();
     *   stage.setScene(new Scene(root));
     *   stage.show();
     * ─────────────────────────────────────────────────────────────────────────
     *
     * @param guestFirstName    guest's first name shown in greeting
     * @param roomPreference    recorded preference, e.g. "feather pillows"
     * @param activeResCount    total number of active reservations
     * @param activeResType     room type of the latest reservation
     * @param accountBalance    amount already paid / settled
     * @param reservationTotal  full cost of the latest reservation
     * @param roomNumber        room number string, e.g. "204"
     * @param reservationStatus "CONFIRMED", "PENDING", or "CANCELLED"
     * @param checkInDate       check-in date
     * @param checkInTime       check-in time (e.g. 15:00)
     * @param checkOutDate      check-out date
     * @param checkOutTime      check-out time (e.g. 11:00)
     */
    public void setSessionData(String    guestFirstName,
                               String    roomPreference,
                               int       activeResCount,
                               String    activeResType,
                               double    accountBalance,
                               double    reservationTotal,
                               String    roomNumber,
                               String    reservationStatus,
                               LocalDate checkInDate,
                               LocalTime checkInTime,
                               LocalDate checkOutDate,
                               LocalTime checkOutTime) {

        this.guestFirstName    = guestFirstName;
        this.roomPreference    = roomPreference;
        this.activeResCount    = activeResCount;
        this.activeResType     = activeResType;
        this.accountBalance    = accountBalance;
        this.reservationTotal  = reservationTotal;
        this.roomNumber        = roomNumber;
        this.reservationStatus = reservationStatus;
        this.checkInDate       = checkInDate;
        this.checkInTime       = checkInTime;
        this.checkOutDate      = checkOutDate;
        this.checkOutTime      = checkOutTime;
    }
    
}
