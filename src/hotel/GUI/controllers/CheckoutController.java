package hotel.GUI.controllers;

import hotel.GUI.utils.SceneManager;
import hotel.GUI.utils.SessionManager;
import hotel.core.BookingEngine;
import hotel.core.Database;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.ReservationStatus;
import hotel.model.users.Guest;
import hotel.model.users.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Grand Azure Hotel – Booking Checkout (3-step flow).
 *
 * Fully integrated with the real backend:
 *   - Reads the active Reservation + Invoice from Database via SessionManager
 *   - Processes payment using Invoice.pay() and BookingEngine.confirmReservation()
 *   - Deducts guest balance for Account Balance payment method
 *   - Marks invoice paid and reservation CONFIRMED on success
 *   - Navigates back to GuestDashboard on completion
 *
 * Steps:
 *   Screen 1 → Review Invoice   (screenReview)
 *   Screen 2 → Payment          (screenPayment)
 *   Screen 3 → Confirmation     (screenConfirmation)
 */
public class CheckoutController implements Initializable {

    // ─── SCREEN 1 – REVIEW INVOICE ──────────────────────────────────────────
    @FXML private VBox   screenReview;
    @FXML private Button btnCancelReview;

    @FXML private Label lblGuestName;
    @FXML private Label lblGuestEmail;
    @FXML private Label lblRoomName;
    @FXML private Label lblRoomMeta;

    @FXML private Label lblRoomRateDesc;
    @FXML private Label lblRoomRateAmt;
    @FXML private Label lblTaxDesc;
    @FXML private Label lblTaxAmt;
    @FXML private Label lblResortFeeDesc;
    @FXML private Label lblResortFeeAmt;
    @FXML private Label lblTotalDue;

    @FXML private Label  lblCheckInDate;
    @FXML private Label  lblCheckInTime;
    @FXML private Label  lblCheckOutDate;
    @FXML private Label  lblCheckOutTime;
    @FXML private Button btnContinueToPayment;

    // ─── SCREEN 2 – PAYMENT ─────────────────────────────────────────────────
    @FXML private VBox   screenPayment;
    @FXML private Label  lblPaymentSubtitle;
    @FXML private Label  lblAvailableBalance;
    @FXML private Button btnPayFromBalance;
    @FXML private Label  lblAmountToPay;
    @FXML private Button btnPayWithCard;
    @FXML private VBox   panelCreditCard;
    @FXML private HBox   panelPayAtHotel;

    @FXML private RadioButton rbCreditCard;
    @FXML private RadioButton rbPayAtHotel;

    @FXML private Button btnBackToReview;
    @FXML private Button btnContinueToConfirm;

    // ─── SCREEN 3 – CONFIRMATION ─────────────────────────────────────────────
    @FXML private VBox   screenConfirmation;
    @FXML private Label  lblReservationId;
    @FXML private Label  lblConfirmGuestName;
    @FXML private Label  lblConfirmRoom;
    @FXML private Label  lblConfirmCheckIn;
    @FXML private Label  lblConfirmTotal;
    @FXML private Button btnDownloadReceipt;
    @FXML private Button btnGoToDashboard;

    // ─── Backend references ──────────────────────────────────────────────────
    private Reservation activeReservation;
    private Invoice     activeInvoice;
    private Guest       loggedInGuest;
    private BookingEngine engine;

    // ─── Payment method state ────────────────────────────────────────────────
    private enum PaymentChoice { CREDIT_CARD, PAY_AT_HOTEL, ACCOUNT_BALANCE }
    private PaymentChoice selectedPayment = PaymentChoice.CREDIT_CARD;

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.US);

    // ─── Static injection point ──────────────────────────────────────────────
    /**
     * Call this from the screen that navigates to Checkout BEFORE navigating,
     * so the controller can pick up the right reservation.
     *
     * Example (from GuestDashboard):
     *   Checkout.setPendingReservation(latestReservation);
     *   SceneManager.navigate("Checkout.fxml");
     */
    private static Reservation pendingReservation = null;
    public static void setPendingReservation(Reservation reservation) {
        pendingReservation = reservation;
    }

    // =========================================================================
    //  INITIALIZE
    // =========================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = new BookingEngine();

        // Resolve logged-in guest
        User user = SessionManager.getLoggedInUser();
        if (user instanceof Guest) {
            loggedInGuest = (Guest) user;
        }

        // Resolve active reservation
        if (pendingReservation != null) {
            activeReservation = pendingReservation;
            pendingReservation = null; // clear after reading
        } else if (loggedInGuest != null) {
            // Fall back: pick the most recent active reservation for this guest
            activeReservation = Database.getReservations().stream()
                    .filter(r -> r.getGuest().getUserName().equals(loggedInGuest.getUserName()))
                    .filter(r -> r.getStatus() == ReservationStatus.PENDING
                              || r.getStatus() == ReservationStatus.CONFIRMED)
                    .reduce((first, second) -> second) // last one
                    .orElse(null);
        }

        if (activeReservation == null) {
            showInfoAlert("No Active Reservation",
                    "No pending reservation was found. Returning to your dashboard.");
            SceneManager.navigate("GuestDashboard.fxml");
            return;
        }

        // Resolve or generate invoice
        activeInvoice = findOrGenerateInvoice(activeReservation);

        populateReviewScreen();
        populatePaymentScreen();
        showScreen(1);
    }

    // =========================================================================
    //  DATA POPULATION
    // =========================================================================

    private void populateReviewScreen() {
        if (loggedInGuest != null) {
            lblGuestName.setText(loggedInGuest.getUserName());
            // Guests don't have email in the model – use phone as secondary identifier
            lblGuestEmail.setText(loggedInGuest.getPhoneNumber() != null
                    ? loggedInGuest.getPhoneNumber()
                    : "—");
        } else {
            lblGuestName.setText(activeReservation.getGuest().getUserName());
            lblGuestEmail.setText("—");
        }

        lblRoomName.setText(activeReservation.getRoom().getRoomType().getTypeName()
                + " – Room " + activeReservation.getRoom().getRoomNumber());
        lblRoomMeta.setText(activeReservation.calcnights() + " Nights  •  "
                + activeReservation.getNumAdults() + " Adults"
                + (activeReservation.getNumChildren() > 0
                        ? ", " + activeReservation.getNumChildren() + " Children" : ""));

        // ── Cost breakdown using real BookingEngine calculations ──────────────
        double roomCost    = engine.calculateRoomCost(
                activeReservation.getRoom(),
                activeReservation.getCheckinDate(),
                activeReservation.getCheckoutDate());
        double diningCost  = engine.calculateDiningCost(
                activeReservation.getDiningpackage(),
                activeReservation.calcnights());
        double amenityCost = engine.calculateAmenityCost(
                activeReservation.getSelectedAmenities());

        double subtotal    = roomCost + diningCost + amenityCost;
        double discount    = activeInvoice != null ? activeInvoice.getDiscountAmount() : 0.0;
        double grandTotal  = activeInvoice != null ? activeInvoice.getTotalAmount() : subtotal;

        // Room rate row
        lblRoomRateDesc.setText("Room Cost ("
                + activeReservation.calcnights() + " nights)");
        lblRoomRateAmt.setText(CURRENCY.format(roomCost));

        // Dining / services row (repurposed from tax row)
        String diningLabel = activeReservation.getDiningpackage() != null
                ? "Dining: " + activeReservation.getDiningpackage().name().replace("_", " ")
                : "Dining Package";
        lblTaxDesc.setText(diningLabel);
        lblTaxAmt.setText(CURRENCY.format(diningCost));

        // Amenities row (repurposed from resort fee row)
        lblResortFeeDesc.setText("Amenities & Add-ons");
        lblResortFeeAmt.setText(CURRENCY.format(amenityCost));

        // Promo / discount label overrides resort fee row if discount exists
        if (discount > 0 && activeInvoice != null
                && activeInvoice.getAppliedPromoCode() != null
                && !activeInvoice.getAppliedPromoCode().equals("NONE")) {
            lblResortFeeDesc.setText("Promo: " + activeInvoice.getAppliedPromoCode()
                    + "  −" + CURRENCY.format(discount));
            lblResortFeeAmt.setText("−" + CURRENCY.format(discount));
            lblResortFeeAmt.setStyle("-fx-font-size: 14px; -fx-text-fill: #2e8b57;");
        }

        lblTotalDue.setText(CURRENCY.format(grandTotal));

        // ── Booking summary ───────────────────────────────────────────────────
        lblCheckInDate.setText(activeReservation.getCheckinDate().toString());
        lblCheckInTime.setText("3:00 PM");   // Standard hotel check-in time
        lblCheckOutDate.setText(activeReservation.getCheckoutDate().toString());
        lblCheckOutTime.setText("11:00 AM"); // Standard hotel check-out time
    }

    private void populatePaymentScreen() {
        double grandTotal = activeInvoice != null
                ? activeInvoice.getTotalAmount()
                : engine.calculateTotalReservationCost(activeReservation);

        lblPaymentSubtitle.setText(
                "Choose how you would like to settle your balance of "
                        + CURRENCY.format(grandTotal) + ".");

        double balance = loggedInGuest != null ? loggedInGuest.getBalance() : 0.0;
        lblAvailableBalance.setText(CURRENCY.format(balance));
        lblAmountToPay.setText(String.format("%.2f", grandTotal));
    }

    // =========================================================================
    //  SCREEN NAVIGATION
    // =========================================================================

    private void showScreen(int step) {
        setScreen(screenReview,       step == 1);
        setScreen(screenPayment,      step == 2);
        setScreen(screenConfirmation, step == 3);
    }

    private void setScreen(VBox screen, boolean active) {
        screen.setVisible(active);
        screen.setManaged(active);
    }

    // =========================================================================
    //  SCREEN 1 – ACTIONS
    // =========================================================================

    @FXML
    private void onContinueToPayment(ActionEvent event) {
        showScreen(2);
    }

    @FXML
    private void onCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel this checkout? Your reservation will remain pending.",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Cancel Checkout");
        alert.setHeaderText("Cancel Booking Checkout");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                SceneManager.navigate("GuestDashboard.fxml");
            }
        });
    }

    // =========================================================================
    //  SCREEN 2 – PAYMENT ACTIONS
    // =========================================================================

    @FXML
    private void onBackToReview(ActionEvent event) {
        showScreen(1);
    }

    @FXML
    private void onSelectCreditCard(ActionEvent event) {
        selectedPayment = PaymentChoice.CREDIT_CARD;
        rbCreditCard.setSelected(true);
        rbPayAtHotel.setSelected(false);
        highlightCreditCardPanel(true);
    }

    @FXML
    private void onSelectPayAtHotel(ActionEvent event) {
        selectedPayment = PaymentChoice.PAY_AT_HOTEL;
        rbPayAtHotel.setSelected(true);
        rbCreditCard.setSelected(false);
        highlightCreditCardPanel(false);
    }

    private void highlightCreditCardPanel(boolean selected) {
        String borderStyle = selected
                ? "-fx-border-color: #2e8b57; -fx-border-width: 2; -fx-border-radius: 10;"
                : "-fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 10;";
        panelCreditCard.setStyle(
                "-fx-background-color: #f7f5f0; -fx-background-radius: 10; " +
                "-fx-padding: 16 20 16 20; " + borderStyle);
    }

    /** "Pay from Balance" – deducts directly from Guest.balance */
    @FXML
    private void onPayFromBalance(ActionEvent event) {
        if (loggedInGuest == null) {
            showInfoAlert("Session Error", "No logged-in guest found.");
            return;
        }

        double total = activeInvoice != null
                ? activeInvoice.getTotalAmount()
                : engine.calculateTotalReservationCost(activeReservation);

        if (loggedInGuest.getBalance() < total) {
            showInfoAlert("Insufficient Balance",
                    "Your account balance of " + CURRENCY.format(loggedInGuest.getBalance())
                    + " is less than the amount due of " + CURRENCY.format(total) + ".\n\n"
                    + "Please top up your balance or choose another payment method.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Pay " + CURRENCY.format(total) + " from your account balance of "
                + CURRENCY.format(loggedInGuest.getBalance()) + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Pay from Account Balance");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                selectedPayment = PaymentChoice.ACCOUNT_BALANCE;
                processPaymentAndConfirm(PaymentMethod.ONLINE); // treat balance as online payment
            }
        });
    }

    /** "Pay with Card" button inside the credit-card panel */
    @FXML
    private void onPayWithCard(ActionEvent event) {
        if (loggedInGuest == null) {
            showInfoAlert("Session Error", "No logged-in guest found.");
            return;
        }

        double total = activeInvoice != null
                ? activeInvoice.getTotalAmount()
                : engine.calculateTotalReservationCost(activeReservation);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Charge " + CURRENCY.format(total) + " to your credit card on file?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Credit Card Payment");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                processPaymentAndConfirm(PaymentMethod.CREDIT_CARD);
            }
        });
    }

    /** "Continue to Confirmation" – only valid for Pay at Hotel */
    @FXML
    private void onContinueToConfirmation(ActionEvent event) {
        if (selectedPayment == PaymentChoice.PAY_AT_HOTEL) {
            // Mark reservation as confirmed but invoice stays unpaid (cash at desk)
            activeReservation.confirmreservation();
            if (activeInvoice != null) {
                activeInvoice.setPaymentMethod(PaymentMethod.CASH);
                activeInvoice.setPaid(false);
                activeInvoice.setPaymentDate(activeReservation.getCheckinDate());
            }
            Database.saveData();
            showScreen(3);
            populateConfirmationScreen();
        } else {
            showInfoAlert("Payment Required",
                    "Please complete payment before continuing to confirmation.\n\n"
                    + "Use 'Pay from Balance' or 'Pay with Card', or select 'Pay at Hotel' "
                    + "if you prefer to pay upon arrival.");
        }
    }

    // ─── Core payment processing ──────────────────────────────────────────────

    /**
     * Processes the actual payment using the real Invoice.pay() and
     * BookingEngine.confirmReservation() backend methods.
     */
    private void processPaymentAndConfirm(PaymentMethod method) {
        try {
            if (activeInvoice == null) {
                // Generate a new invoice if one doesn't exist
                activeInvoice = engine.generateInvoice(activeReservation, null);
            }

            // Use Invoice.pay() which handles balance deduction and sets isPaid flag
            activeInvoice.pay(loggedInGuest, method);

            if (!activeInvoice.isPaid()) {
                showInfoAlert("Payment Failed",
                        "Payment could not be processed. Please check your balance and try again.");
                return;
            }

            // Confirm the reservation using BookingEngine
            boolean confirmed = engine.confirmReservation(
                    activeReservation.getReservationID(), method);

            if (!confirmed) {
                // reservations that are already CONFIRMED still need updating
                activeReservation.confirmreservation();
            }

            Database.saveData();

            populateConfirmationScreen();
            showScreen(3);

        } catch (Exception e) {
            showInfoAlert("Payment Error", "An error occurred: " + e.getMessage());
            System.err.println("[Checkout] Payment error: " + e.getMessage());
        }
    }

    // =========================================================================
    //  SCREEN 3 – CONFIRMATION
    // =========================================================================

    private void populateConfirmationScreen() {
        String resId = "#RES-" + activeReservation.getReservationID();
        lblReservationId.setText(resId);
        lblConfirmGuestName.setText(loggedInGuest != null
                ? loggedInGuest.getUserName()
                : activeReservation.getGuest().getUserName());
        lblConfirmRoom.setText(activeReservation.getRoom().getRoomType().getTypeName()
                + " " + activeReservation.getRoom().getRoomNumber());
        lblConfirmCheckIn.setText(activeReservation.getCheckinDate().toString());
        double total = activeInvoice != null
                ? activeInvoice.getTotalAmount()
                : engine.calculateTotalReservationCost(activeReservation);
        lblConfirmTotal.setText(CURRENCY.format(total));
    }

    @FXML
    private void onDownloadReceipt(ActionEvent event) {
        double total = activeInvoice != null
                ? activeInvoice.getTotalAmount()
                : engine.calculateTotalReservationCost(activeReservation);

        String summary = activeInvoice != null
                ? activeInvoice.generateItemizedSummary()
                : "Invoice summary unavailable.";

        showInfoAlert("Booking Receipt",
                "Reservation: #RES-" + activeReservation.getReservationID() + "\n"
                + "Guest:     " + (loggedInGuest != null ? loggedInGuest.getUserName() : "—") + "\n"
                + "Room:      " + activeReservation.getRoom().getRoomType().getTypeName()
                + " " + activeReservation.getRoom().getRoomNumber() + "\n"
                + "Check-in:  " + activeReservation.getCheckinDate() + "\n"
                + "Check-out: " + activeReservation.getCheckoutDate() + "\n"
                + "Total Paid: " + CURRENCY.format(total) + "\n\n"
                + summary);
    }

    @FXML
    private void onGoToDashboard(ActionEvent event) {
        SceneManager.navigate("GuestDashboard.fxml");
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    /**
     * Finds an existing invoice for the reservation or generates one.
     * Mirrors the same logic used in GuestDashboard.
     */
    private Invoice findOrGenerateInvoice(Reservation reservation) {
        Optional<Invoice> existing = Database.getInvoices().stream()
                .filter(inv -> inv.getReservation() != null
                        && inv.getReservation().getReservationID() == reservation.getReservationID())
                .findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }
        return engine.generateInvoice(reservation, null);
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
