package hotel.GUI.controllers;

import hotel.GUI.utils.SessionManager;
import hotel.core.BookingEngine;
import hotel.core.Database;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;
import hotel.model.enums.ReservationStatus;
import hotel.model.users.Guest;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuestDashboard {

    // Nested Controllers
    @FXML private SideBarController sideBarController;
    @FXML private TopBarController topBarController;

    // Stats row
    @FXML private Label lblGreeting;
    @FXML private Label lblActiveReservations;
    @FXML private Label lblBalance;
    @FXML private Label lblNextCheckIn;

    // Reservation card
    @FXML private HBox latestResBox;
    @FXML private Label lblRoomType;
    @FXML private Label lblCheckInDate;
    @FXML private Label lblCheckOutDate;
    @FXML private ImageView roomImageView;

    // Modify Stay panel
    @FXML private VBox modifyStayPanel;
    @FXML private DatePicker dpNewCheckIn;
    @FXML private DatePicker dpNewCheckOut;
    @FXML private Label lblAvailability;
    @FXML private HBox modifyConfirmRow;
    @FXML private Label lblNewNights;
    @FXML private Label lblNewCost;
    @FXML private Label lblCostDiff;

    // Invoice panel
    @FXML private VBox invoicePanel;
    @FXML private Label lblInvoiceTitle;
    @FXML private Label lblInvoiceStatus;
    @FXML private Label lblInvRoom;
    @FXML private Label lblInvCheckIn;
    @FXML private Label lblInvCheckOut;
    @FXML private Label lblInvNights;
    @FXML private Label lblInvDining;
    @FXML private Label lblInvGuests;
    @FXML private Label lblInvPayment;
    @FXML private Label lblInvPaymentDate;
    @FXML private Label lblInvRoomCost;
    @FXML private Label lblInvDiningCost;
    @FXML private Label lblInvAmenityCost;
    @FXML private Label lblInvPromo;
    @FXML private Label lblInvDiscount;
    @FXML private Label lblInvTotal;

    private BookingEngine engine;
    private Reservation latestReservation;

    private static final String ROOMS_ASSET_PATH = "/hotel/GUI/assets/rooms/";
    private static final String[] EXTENSIONS = {".jpg", ".jpg.jpg", ".png", ".jpeg"};
    private static final String[] DEFAULT_CANDIDATES = {
            ROOMS_ASSET_PATH + "default.jpg",
            ROOMS_ASSET_PATH + "default.jpg.png",
            ROOMS_ASSET_PATH + "default.png"
    };

    @FXML
    public void initialize() {
        engine = new BookingEngine();

        if (sideBarController != null) {
            sideBarController.setRole("GUEST");
            sideBarController.setActiveSection("concierge");
        }

        if (topBarController != null) {
            topBarController.setPageTitle("The Digital Concierge", "Dashboard");
            topBarController.refresh();
        }

        Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
        if (activeGuest != null) {
            populateDashboard(activeGuest);
        }
    }

    // -------------------------------------------------------------------------
    // Data population
    // -------------------------------------------------------------------------

    private void populateDashboard(Guest guest) {
        int hour = LocalTime.now().getHour();
        String timeGreeting = (hour < 12) ? "Good Morning" : (hour < 17) ? "Good Afternoon" : "Good Evening";
        lblGreeting.setText(timeGreeting + ", " + guest.getUserName());
        lblBalance.setText(String.format("%,.2f", guest.getBalance()));

        List<Reservation> guestRes = engine.getReservationsForGuest(guest)
                .stream()
                .filter(r -> r.getGuest().equals(guest))
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED
                        || r.getStatus() == ReservationStatus.PENDING)
                .collect(Collectors.toList());

        lblActiveReservations.setText(String.valueOf(guestRes.size()));

        if (!guestRes.isEmpty()) {
            latestReservation = guestRes.get(guestRes.size() - 1);

            lblRoomType.setText(latestReservation.getRoom().getRoomType().getTypeName());
            lblCheckInDate.setText(latestReservation.getCheckinDate().toString());
            lblCheckOutDate.setText(latestReservation.getCheckoutDate().toString());

            guestRes.stream()
                    .filter(r -> r.getCheckinDate().isAfter(LocalDate.now()))
                    .min((a, b) -> a.getCheckinDate().compareTo(b.getCheckinDate()))
                    .ifPresentOrElse(
                            r -> lblNextCheckIn.setText(r.getCheckinDate().toString()),
                            () -> lblNextCheckIn.setText("Currently Checked In")
                    );

            loadRoomImage(latestReservation.getRoom().getRoomType().getTypeName());
            latestResBox.setDisable(false);
            latestResBox.setOpacity(1.0);

        } else {
            latestReservation = null;
            lblRoomType.setText("No Active Bookings");
            lblCheckInDate.setText("-");
            lblCheckOutDate.setText("-");
            lblNextCheckIn.setText("None");
            loadRoomImage(null);
            latestResBox.setDisable(true);
            latestResBox.setOpacity(0.5);
        }

        hideInvoicePanel();
        hideModifyStayPanel();
    }

    // -------------------------------------------------------------------------
    // Image loading
    // -------------------------------------------------------------------------

    private void loadRoomImage(String typeName) {
        if (roomImageView == null) return;
        try {
            java.io.InputStream imageStream = null;

            if (typeName != null && !typeName.isBlank()) {
                for (String ext : EXTENSIONS) {
                    String path = ROOMS_ASSET_PATH + typeName + ext;
                    imageStream = getClass().getResourceAsStream(path);
                    if (imageStream != null) {
                        System.out.println("GuestDashboard: loaded image -> " + path);
                        break;
                    }
                }
            }

            if (imageStream == null) {
                for (String candidate : DEFAULT_CANDIDATES) {
                    imageStream = getClass().getResourceAsStream(candidate);
                    if (imageStream != null) break;
                }
            }

            if (imageStream != null) {
                roomImageView.setImage(new Image(imageStream));
            }
        } catch (Exception e) {
            System.err.println("GuestDashboard: failed to load room image — " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Modify Stay panel
    // -------------------------------------------------------------------------

    private void showModifyStayPanel() {
        if (modifyStayPanel == null) return;

        // Pre-fill pickers with current reservation dates
        if (latestReservation != null) {
            dpNewCheckIn.setValue(latestReservation.getCheckinDate());
            dpNewCheckOut.setValue(latestReservation.getCheckoutDate());
        }

        // Reset state
        lblAvailability.setText("—");
        lblAvailability.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        modifyConfirmRow.setVisible(false);
        modifyConfirmRow.setManaged(false);

        modifyStayPanel.setVisible(true);
        modifyStayPanel.setManaged(true);

        // Close invoice panel if open
        hideInvoicePanel();
    }

    private void hideModifyStayPanel() {
        if (modifyStayPanel == null) return;
        modifyStayPanel.setVisible(false);
        modifyStayPanel.setManaged(false);
    }

    @FXML
    private void onCheckAvailability() {
        if (latestReservation == null) return;

        LocalDate newCheckIn  = dpNewCheckIn.getValue();
        LocalDate newCheckOut = dpNewCheckOut.getValue();

        // --- Validation ---
        if (newCheckIn == null || newCheckOut == null) {
            lblAvailability.setText("⚠ Please select both dates.");
            lblAvailability.setStyle("-fx-text-fill: #b9120f; -fx-font-weight: bold;");
            modifyConfirmRow.setVisible(false);
            modifyConfirmRow.setManaged(false);
            return;
        }

        if (!newCheckOut.isAfter(newCheckIn)) {
            lblAvailability.setText("⚠ Check-out must be after check-in.");
            lblAvailability.setStyle("-fx-text-fill: #b9120f; -fx-font-weight: bold;");
            modifyConfirmRow.setVisible(false);
            modifyConfirmRow.setManaged(false);
            return;
        }

        if (newCheckIn.equals(latestReservation.getCheckinDate())
                && newCheckOut.equals(latestReservation.getCheckoutDate())) {
            lblAvailability.setText("⚠ Dates are the same as current booking.");
            lblAvailability.setStyle("-fx-text-fill: #b9120f; -fx-font-weight: bold;");
            modifyConfirmRow.setVisible(false);
            modifyConfirmRow.setManaged(false);
            return;
        }

        // --- Check if room is available for new dates (excluding current reservation) ---
        boolean roomAvailable = Database.getReservations().stream()
                .filter(r -> r.getRoom().getRoomNumber() == latestReservation.getRoom().getRoomNumber())
                .filter(r -> r.getReservationID() != latestReservation.getReservationID()) // exclude self
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED
                        || r.getStatus() == ReservationStatus.PENDING)
                .noneMatch(r -> !(newCheckOut.isBefore(r.getCheckinDate())
                        || newCheckOut.isEqual(r.getCheckinDate())
                        || newCheckIn.isAfter(r.getCheckoutDate())
                        || newCheckIn.isEqual(r.getCheckoutDate())));

        if (!roomAvailable) {
            lblAvailability.setText("✗ Room not available for selected dates.");
            lblAvailability.setStyle("-fx-text-fill: #b9120f; -fx-font-weight: bold;");
            modifyConfirmRow.setVisible(false);
            modifyConfirmRow.setManaged(false);
            return;
        }

        // --- Room is available — show cost preview ---
        lblAvailability.setText("✔ Available!");
        lblAvailability.setStyle("-fx-text-fill: #2D5A27; -fx-font-weight: bold;");

        long newNights = java.time.temporal.ChronoUnit.DAYS.between(newCheckIn, newCheckOut);
        double newRoomCost = engine.calculateRoomCost(latestReservation.getRoom(), newCheckIn, newCheckOut);
        double oldRoomCost = engine.calculateRoomCost(latestReservation.getRoom(),
                latestReservation.getCheckinDate(), latestReservation.getCheckoutDate());
        double diff = newRoomCost - oldRoomCost;

        lblNewNights.setText(newNights + " night(s)");
        lblNewCost.setText(String.format("EGP %,.2f", newRoomCost));

        if (diff > 0) {
            lblCostDiff.setText(String.format("+ EGP %,.2f", diff));
            lblCostDiff.setStyle("-fx-font-weight: bold; -fx-text-fill: #b9120f;");
        } else if (diff < 0) {
            lblCostDiff.setText(String.format("- EGP %,.2f", Math.abs(diff)));
            lblCostDiff.setStyle("-fx-font-weight: bold; -fx-text-fill: #2D5A27;");
        } else {
            lblCostDiff.setText("No change");
            lblCostDiff.setStyle("-fx-font-weight: bold;");
        }

        modifyConfirmRow.setVisible(true);
        modifyConfirmRow.setManaged(true);
    }

    @FXML
    private void onConfirmModify() {
        if (latestReservation == null) return;

        LocalDate newCheckIn  = dpNewCheckIn.getValue();
        LocalDate newCheckOut = dpNewCheckOut.getValue();

        if (newCheckIn == null || newCheckOut == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Date Change");
        confirm.setHeaderText("Are you sure you want to change your stay dates?");
        confirm.setContentText(
                "From:  " + latestReservation.getCheckinDate() + "  →  " + latestReservation.getCheckoutDate()
                        + "\nTo:    " + newCheckIn + "  →  " + newCheckOut
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            // 1. Update reservation dates
            latestReservation.setCheckinDate(newCheckIn);
            latestReservation.setCheckoutDate(newCheckOut);

            // 2. Find the existing invoice for this reservation and update its total
            //    so the invoice stays in sync with the new dates
            Database.getInvoices().stream()
                    .filter(inv -> inv.getReservation() != null
                            && inv.getReservation().getReservationID() == latestReservation.getReservationID())
                    .findFirst()
                    .ifPresent(inv -> {
                        double roomCost    = engine.calculateRoomCost(
                                latestReservation.getRoom(), newCheckIn, newCheckOut);
                        double diningCost  = engine.calculateDiningCost(
                                latestReservation.getDiningpackage(), latestReservation.calcnights());
                        double amenityCost = engine.calculateAmenityCost(
                                latestReservation.getSelectedAmenities());

                        double subtotal = roomCost + diningCost + amenityCost;

                        // Re-apply existing discount if a promo was used
                        double discount = 0.0;
                        if (inv.getAppliedPromoCode() != null
                                && !inv.getAppliedPromoCode().equals("NONE")) {
                            double multiplier = engine.validatePromocode(inv.getAppliedPromoCode());
                            discount = subtotal - (subtotal * multiplier);
                        }

                        inv.setDiscountAmount(discount);
                        inv.setTotalAmount(subtotal - discount);

                        System.out.println("GuestDashboard: invoice #" + inv.getInvoiceID()
                                + " updated -> new total EGP " + inv.getTotalAmount());
                    });

            // 3. Persist everything to disk
            Database.saveData();

            showInfo("Stay Updated",
                    "Your stay has been updated successfully!\n"
                            + "New check-in:  " + newCheckIn + "\n"
                            + "New check-out: " + newCheckOut + "\n"
                            + "Invoice has been recalculated.");

            // 4. Refresh dashboard — labels will now show the new dates
            Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
            if (activeGuest != null) {
                populateDashboard(activeGuest);
            }
        }
    }

    @FXML
    private void onCloseModifyStay() {
        hideModifyStayPanel();
    }

    // -------------------------------------------------------------------------
    // Invoice lookup
    // -------------------------------------------------------------------------

    private Invoice findOrGenerateInvoice(Reservation reservation) {
        Optional<Invoice> existing = Database.getInvoices()
                .stream()
                .filter(inv -> inv.getReservation() != null
                        && inv.getReservation().getReservationID() == reservation.getReservationID())
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        return engine.generateInvoice(reservation, null);
    }

    // -------------------------------------------------------------------------
    // Invoice panel
    // -------------------------------------------------------------------------

    private void showInvoicePanel(Invoice invoice) {
        if (invoicePanel == null || invoice == null) return;

        Reservation res = latestReservation;

        lblInvoiceTitle.setText("Invoice  #" + invoice.getInvoiceID()
                + "   —   Reservation #" + res.getReservationID());
        lblInvoiceStatus.setText(invoice.isPaid() ? "✔  PAID" : "⚠  UNPAID");
        lblInvoiceStatus.setStyle(invoice.isPaid()
                ? "-fx-text-fill: #90EE90;" : "-fx-text-fill: #FFD700;");

        lblInvRoom.setText(res.getRoom().getRoomType().getTypeName()
                + "  (Room " + res.getRoom().getRoomNumber() + ")");
        lblInvCheckIn.setText(res.getCheckinDate().toString());
        lblInvCheckOut.setText(res.getCheckoutDate().toString());
        lblInvNights.setText(res.calcnights() + " night(s)");
        lblInvDining.setText(res.getDiningpackage() != null ? res.getDiningpackage().toString() : "None");
        lblInvGuests.setText(res.getNumAdults() + " adult(s)"
                + (res.getNumChildren() > 0 ? ", " + res.getNumChildren() + " child(ren)" : ""));
        lblInvPayment.setText(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod().toString() : "—");
        lblInvPaymentDate.setText(invoice.getPaymentDate() != null ? invoice.getPaymentDate().toString() : "—");

        double roomCost    = engine.calculateRoomCost(res.getRoom(), res.getCheckinDate(), res.getCheckoutDate());
        double diningCost  = engine.calculateDiningCost(res.getDiningpackage(), res.calcnights());
        double amenityCost = engine.calculateAmenityCost(res.getSelectedAmenities());

        lblInvRoomCost.setText(String.format("EGP %,.2f", roomCost));
        lblInvDiningCost.setText(String.format("EGP %,.2f", diningCost));
        lblInvAmenityCost.setText(String.format("EGP %,.2f", amenityCost));
        lblInvPromo.setText(invoice.getAppliedPromoCode() != null
                && !invoice.getAppliedPromoCode().equals("NONE")
                ? invoice.getAppliedPromoCode() : "No promo applied");
        lblInvDiscount.setText(String.format("- EGP %,.2f", invoice.getDiscountAmount()));
        lblInvTotal.setText(String.format("EGP %,.2f", invoice.getTotalAmount()));

        invoicePanel.setVisible(true);
        invoicePanel.setManaged(true);
    }

    private void hideInvoicePanel() {
        if (invoicePanel == null) return;
        invoicePanel.setVisible(false);
        invoicePanel.setManaged(false);
    }

    // -------------------------------------------------------------------------
    // FXML action handlers
    // -------------------------------------------------------------------------

    @FXML
    private void onModifyStay() {
        if (latestReservation == null) {
            showInfo("No Reservation", "You have no active reservation to modify.");
            return;
        }
        // Toggle the panel
        if (modifyStayPanel.isVisible()) {
            hideModifyStayPanel();
        } else {
            showModifyStayPanel();
        }
    }

    @FXML
    private void onViewInvoice() {
        if (latestReservation == null) return;

        if (invoicePanel.isVisible()) {
            hideInvoicePanel();
            return;
        }

        Invoice invoice = findOrGenerateInvoice(latestReservation);
        if (invoice == null) {
            showError("Invoice Not Found", "No invoice was found for this reservation.");
            return;
        }

        showInvoicePanel(invoice);
        hideModifyStayPanel();
    }

    @FXML
    private void onCloseInvoice() {
        hideInvoicePanel();
    }

    @FXML
    private void onCancelReservation() {
        if (latestReservation == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Reservation");
        confirm.setHeaderText("Are you sure you want to cancel?");
        confirm.setContentText(
                "Reservation #" + latestReservation.getReservationID()
                        + " — " + latestReservation.getRoom().getRoomType().getTypeName()
                        + "\nCheck-in:  " + latestReservation.getCheckinDate()
                        + "\nCheck-out: " + latestReservation.getCheckoutDate()
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            engine.processCancellation(latestReservation.getReservationID(), LocalDate.now());
            showInfo("Reservation Cancelled", "Your reservation has been successfully cancelled.");

            Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
            if (activeGuest != null) populateDashboard(activeGuest);
        }
    }

    // -------------------------------------------------------------------------
    // Alert helpers
    // -------------------------------------------------------------------------

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
