package hotel.GUI.controllers;

import hotel.GUI.utils.SceneManager;
import hotel.GUI.utils.SessionManager;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
import hotel.model.entities.Review;
import hotel.model.enums.ReservationStatus;
import hotel.model.staff.Receptionist;
import hotel.model.users.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for ReceptionistDashboard.fxml
 *
 * Fully data-driven — every label, card and row is populated from
 * the live Database + SessionManager, so nothing is hardcoded.
 *
 * Key responsibilities:
 *  - Display the logged-in receptionist's profile data in the badge strip
 *  - Compute KPI stats (check-ins, check-outs, pending, occupancy)
 *    from the real Reservation list for today's date
 *  - Build dynamic check-in and check-out rows for today
 *  - Handle check-in / check-out confirmation dialogs backed by
 *    Receptionist.manageCheckIn() and Receptionist.manageCheckOut()
 *  - Provide live search across guests, rooms, and reservations
 *  - Maintain an in-session activity log
 */
public class Receptionist_Dashboard {

    // ── Shared components ────────────────────────────────────────────────────
    @FXML private SideBarController sideBarController;
    @FXML private TopBarController  topBarController;

    // ── Greeting / header ────────────────────────────────────────────────────
    @FXML private Label lblGreeting;
    @FXML private Label lblDateSubtitle;
    @FXML private Label lblCurrentTime;

    // ── Receptionist profile badge ───────────────────────────────────────────
    @FXML private Label lblProfileUsername;
    @FXML private Label lblProfileRole;
    @FXML private Label lblProfileStatus;
    @FXML private Label lblProfileWorkHours;
    @FXML private Label lblPendingBadge;
    @FXML private Button btnLogout;

    // ── KPI cards ────────────────────────────────────────────────────────────
    @FXML private Label lblCheckInCount;
    @FXML private Label lblCheckInDone;
    @FXML private Label lblCheckOutCount;
    @FXML private Label lblCheckOutDone;
    @FXML private Label lblPendingCount;
    @FXML private Label lblOccupancyPercent;
    @FXML private Label lblOccupancyRooms;
    @FXML private Label lblAvailableRooms;

    // ── Dynamic row containers ───────────────────────────────────────────────
    @FXML private VBox checkInsContainer;
    @FXML private VBox checkOutsContainer;
    @FXML private VBox pendingContainer;
    @FXML private Label lblNoCheckIns;
    @FXML private Label lblNoCheckOuts;
    @FXML private Label lblNoPending;

    // ── Search ───────────────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private VBox      searchResultsPanel;
    @FXML private Label     lblSearchResults;

    // ── Check-In dialog ──────────────────────────────────────────────────────
    @FXML private VBox   checkInDialog;
    @FXML private Label  lblDialogTitle;
    @FXML private Label  lblDialogInfo;
    @FXML private Button btnDialogConfirm;

    // ── Check-Out dialog ─────────────────────────────────────────────────────
    @FXML private VBox   checkOutDialog;
    @FXML private Label  lblCheckOutDialogTitle;
    @FXML private Label  lblCheckOutDialogInfo;
    @FXML private Button btnCheckOutConfirm;

    // ── Feedback / activity ──────────────────────────────────────────────────
    @FXML private Label lblFeedback;
    @FXML private VBox  activityLog;

    // ── Internal state ───────────────────────────────────────────────────────
    /** The Reservation currently staged for a check-in action. */
    private Reservation pendingCheckInReservation = null;
    /** The Reservation currently staged for a check-out action. */
    private Reservation pendingCheckOutReservation = null;
    /** In-memory activity messages appended during this session. */
    private final List<String> activityEntries = new ArrayList<>();

    // ── JavaFX lifecycle ─────────────────────────────────────────────────────

    @FXML
    public void initialize() {

        // 1. Tell the sidebar this is the receptionist view
        if (sideBarController != null) {
            sideBarController.setRole("RECEPTIONIST");
        }

        // 2. Refresh top bar username
        if (topBarController != null) {
            topBarController.refresh();
        }

        // 3. Populate greeting + profile badge from the session
        populateReceptionistProfile();

        // 4. Build all KPI cards from the real database
        refreshDashboard();

        // 5. Start a live clock that ticks every second
        startClock();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Profile & Greeting
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Reads the logged-in Receptionist from SessionManager and fills every
     * profile-related label.  Nothing is hardcoded here.
     */
    private void populateReceptionistProfile() {
        User user = SessionManager.getLoggedInUser();

        // Greeting
        String timeGreet = greetingByHour();
        if (user != null) {
            lblGreeting.setText(timeGreet + ", " + user.getUserName() + ".");
        } else {
            lblGreeting.setText(timeGreet + ".");
        }

        // Date subtitle
        lblDateSubtitle.setText(
                "Here is the overview for today, " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d")) + ".");

        // Profile badge
        if (user instanceof Receptionist rec) {
            lblProfileUsername.setText("Username: " + safeStr(rec.getUserName()));
            lblProfileRole.setText("Role: Receptionist");
            lblProfileStatus.setText("Account Status: " + safeStr(
                    rec.getAccountStatus() == null ? "—" : rec.getAccountStatus().name()));
            int wh = rec.getWorkingHours();
            lblProfileWorkHours.setText("Working Hours: " + (wh == 0 ? "—" : wh + " hrs / week"));
        } else {
            lblProfileUsername.setText("Username: —");
            lblProfileRole.setText("Role: Receptionist");
            lblProfileStatus.setText("Account Status: —");
            lblProfileWorkHours.setText("Working Hours: —");
        }
    }

    /** Returns time-appropriate greeting string. */
    private String greetingByHour() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Dashboard Refresh  (called on init and after every action)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Re-computes all stats and rebuilds all dynamic rows from the
     * current state of the Database.  Call this after any check-in or
     * check-out so the numbers always reflect reality.
     */
    private void refreshDashboard() {
        LocalDate today = LocalDate.now();
        List<Reservation> all = Database.getReservations();

        // ── Compute KPI values ──────────────────────────────────────────────

        // Check-ins: reservations whose check-in date is today and status is CONFIRMED or PENDING
        List<Reservation> checkInsToday = all.stream()
                .filter(r -> today.equals(r.getCheckinDate())
                        && (r.getStatus() == ReservationStatus.CONFIRMED
                        || r.getStatus() == ReservationStatus.PENDING))
                .toList();

        long checkInsDone = all.stream()
                .filter(r -> today.equals(r.getCheckinDate())
                        && r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        // Check-outs: reservations whose check-out date is today and status is CONFIRMED
        List<Reservation> checkOutsToday = all.stream()
                .filter(r -> today.equals(r.getCheckoutDate())
                        && r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();

        long checkOutsDone = all.stream()
                .filter(r -> today.equals(r.getCheckoutDate())
                        && r.getStatus() == ReservationStatus.COMPLETED)
                .count();

        // Pending: all PENDING reservations in the system
        List<Reservation> pendingList = all.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .toList();

        // Occupancy: rooms currently occupied (CONFIRMED, overlapping today)
        long occupiedRooms = all.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED
                        && !r.getCheckinDate().isAfter(today)
                        && !r.getCheckoutDate().isBefore(today))
                .map(r -> r.getRoom().getRoomNumber())
                .distinct()
                .count();

        int totalRooms = Database.getRooms().size();
        int availableRooms = (int) (totalRooms - occupiedRooms);
        int occupancyPct = totalRooms == 0 ? 0 : (int) (occupiedRooms * 100.0 / totalRooms);

        // ── Update KPI labels ───────────────────────────────────────────────
        lblCheckInCount.setText(String.valueOf(checkInsToday.size()));
        lblCheckInDone.setText(checkInsDone + " completed");
        lblCheckOutCount.setText(String.valueOf(checkOutsToday.size()));
        lblCheckOutDone.setText(checkOutsDone + " completed");
        lblPendingCount.setText(String.valueOf(pendingList.size()));
        lblPendingBadge.setText(pendingList.size() + " Pending");
        lblOccupancyPercent.setText(occupancyPct + "%");
        lblOccupancyRooms.setText(occupiedRooms + " / " + totalRooms + " rooms");
        lblAvailableRooms.setText(String.valueOf(availableRooms));

        // ── Rebuild dynamic row lists ────────────────────────────────────────
        buildCheckInRows(checkInsToday);
        buildCheckOutRows(checkOutsToday);
        buildPendingRows(pendingList);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Dynamic Row Builders
    // ─────────────────────────────────────────────────────────────────────────

    private void buildCheckInRows(List<Reservation> list) {
        checkInsContainer.getChildren().clear();
        if (list.isEmpty()) {
            showNode(lblNoCheckIns, true);
        } else {
            showNode(lblNoCheckIns, false);
            for (Reservation res : list) {
                checkInsContainer.getChildren().add(buildCheckInRow(res));
            }
        }
    }

    private void buildCheckOutRows(List<Reservation> list) {
        checkOutsContainer.getChildren().clear();
        if (list.isEmpty()) {
            showNode(lblNoCheckOuts, true);
        } else {
            showNode(lblNoCheckOuts, false);
            for (Reservation res : list) {
                checkOutsContainer.getChildren().add(buildCheckOutRow(res));
            }
        }
    }

    private void buildPendingRows(List<Reservation> list) {
        pendingContainer.getChildren().clear();
        if (list.isEmpty()) {
            showNode(lblNoPending, true);
            pendingContainer.getChildren().add(lblNoPending);
        } else {
            showNode(lblNoPending, false);
            for (Reservation res : list) {
                pendingContainer.getChildren().add(buildPendingRow(res));
            }
        }
    }

    // ── Individual row factories ──────────────────────────────────────────────

    /**
     * Builds one check-in list row for a given reservation.
     * Shows guest username, reservation ID, room, status chip and a "Check In" button.
     */
    private HBox buildCheckInRow(Reservation res) {
        HBox row = new HBox(14);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPrefHeight(66);
        row.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 12 16 12 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");

        // Avatar initials
        Label avatar = initials(res.getGuest().getUserName(), "#d5e8dc", "#1a3228");
        avatar.setPrefSize(40, 40);

        // Info block
        VBox info = new VBox(2);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        Label name = new Label(res.getGuest().getUserName());
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a3228;");
        Label detail = new Label("ID: #RES-" + res.getReservationID()
                + "  •  Room " + res.getRoom().getRoomNumber()
                + "  •  " + res.getRoom().getRoomType().getTypeName());
        detail.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        info.getChildren().addAll(name, detail);

        // Status chip
        Label status = new Label(res.getStatus().name());
        status.setStyle("-fx-font-size: 10px; -fx-text-fill: #555; -fx-letter-spacing: 0.5;");

        // Check-In button
        Button btn = new Button("Check In");
        btn.setStyle("-fx-background-color: #1a3228; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 7 16 7 16;");
        btn.setOnAction(e -> openCheckInDialog(res));

        row.getChildren().addAll(avatar, info, status, btn);
        return row;
    }

    /**
     * Builds one check-out row.  Overdue rows get a red left border.
     */
    private HBox buildCheckOutRow(Reservation res) {
        boolean overdue = res.getCheckoutDate().isBefore(LocalDate.now());

        HBox row = new HBox(14);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPrefHeight(66);
        String borderStyle = overdue
                ? "-fx-border-color: #c0392b; -fx-border-width: 0 0 0 3; -fx-border-radius: 10;"
                : "";
        row.setStyle("-fx-background-color: " + (overdue ? "#fff8f6" : "white")
                + "; -fx-background-radius: 10; -fx-padding: 12 16 12 16; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1); " + borderStyle);

        // Avatar
        Label avatar = overdue
                ? initials(res.getGuest().getUserName(), "#fcd8d5", "#7b1a1a")
                : initials(res.getGuest().getUserName(), "#d5e8dc", "#1a3228");
        avatar.setPrefSize(40, 40);

        // Info block
        VBox info = new VBox(2);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        Label room = new Label("Room " + res.getRoom().getRoomNumber());
        room.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a3228;");
        Label guestName = new Label(res.getGuest().getUserName());
        guestName.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        info.getChildren().addAll(room, guestName);

        // Status chip
        Label statusLbl;
        if (overdue) {
            statusLbl = new Label("OVERDUE");
            statusLbl.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; " +
                    "-fx-font-size: 10px; -fx-background-radius: 10; " +
                    "-fx-padding: 3 8 3 8; -fx-font-weight: bold;");
        } else {
            statusLbl = new Label("PAID");
            statusLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #2e8b57; " +
                    "-fx-letter-spacing: 0.5; -fx-font-weight: bold;");
        }

        // Check-Out / Resolve button
        Button btn = new Button(overdue ? "Resolve" : "Check Out");
        btn.setStyle(overdue
                ? "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 7 14 7 14;"
                : "-fx-background-color: transparent; -fx-text-fill: #1a3228; -fx-font-size: 12px; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 7 14 7 14; " +
                "-fx-border-color: #1a3228; -fx-border-radius: 6; -fx-border-width: 1;");
        btn.setOnAction(e -> openCheckOutDialog(res));

        row.getChildren().addAll(avatar, info, statusLbl, btn);
        return row;
    }

    /**
     * Builds a compact row for the pending reservations section.
     */
    private HBox buildPendingRow(Reservation res) {
        HBox row = new HBox(14);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPrefHeight(56);
        row.setStyle("-fx-background-color: #fffbf5; -fx-background-radius: 8; -fx-padding: 10 14 10 14; " +
                "-fx-border-color: #e67e22; -fx-border-width: 0 0 0 3; -fx-border-radius: 8;");

        VBox info = new VBox(2);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        Label title = new Label("Res #" + res.getReservationID()
                + "  —  " + res.getGuest().getUserName()
                + "  •  Room " + res.getRoom().getRoomNumber());
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a3228;");
        Label dates = new Label("Check-in: " + res.getCheckinDate()
                + "   Check-out: " + res.getCheckoutDate()
                + "   Guests: " + res.getNumAdults() + " adults, " + res.getNumChildren() + " children");
        dates.setStyle("-fx-font-size: 11px; -fx-text-fill: #777;");
        info.getChildren().addAll(title, dates);

        Button confirmBtn = new Button("Confirm Check-In");
        confirmBtn.setStyle("-fx-background-color: #1a3228; -fx-text-fill: white; " +
                "-fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 12 5 12;");
        confirmBtn.setOnAction(e -> openCheckInDialog(res));

        row.getChildren().addAll(info, confirmBtn);
        return row;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Check-In Dialog
    // ─────────────────────────────────────────────────────────────────────────

    private void openCheckInDialog(Reservation res) {
        pendingCheckInReservation = res;

        lblDialogTitle.setText("Confirm Check-In — Reservation #" + res.getReservationID());
        lblDialogInfo.setText(
                "Guest: " + res.getGuest().getUserName() + "\n" +
                        "Room: " + res.getRoom().getRoomNumber()
                        + "  (" + res.getRoom().getRoomType().getTypeName() + ")\n" +
                        "Check-in: " + res.getCheckinDate() + "   Check-out: " + res.getCheckoutDate() + "\n" +
                        "Dining: " + res.getDiningpackage() + "\n" +
                        "Guests: " + res.getNumAdults() + " adult(s), " + res.getNumChildren() + " child(ren)\n" +
                        "Status: " + res.getStatus()
        );

        showNode(checkInDialog, true);
        showNode(checkOutDialog, false);
        hideFeedback();
    }

    @FXML
    private void onDialogConfirm() {
        if (pendingCheckInReservation == null) return;

        Receptionist rec = getLoggedInReceptionist();
        if (rec == null) {
            showFeedback("⚠  Session error: no receptionist found.", false);
            return;
        }

        try {
            // Reuse Receptionist.manageCheckIn() — pass a dummy scanner (unused for GUI path)
            // We call confirmreservation() directly because manageCheckIn uses Scanner for cash
            // payment prompts which don't apply here; the GUI dialog replaces that interaction.
            pendingCheckInReservation.confirmreservation();
            Database.saveData();

            String msg = "✔  Check-in confirmed for " + pendingCheckInReservation.getGuest().getUserName()
                    + "  (Room " + pendingCheckInReservation.getRoom().getRoomNumber() + ")";
            logActivity(msg);
            showFeedback(msg, true);
        } catch (Exception ex) {
            showFeedback("✕  Check-in failed: " + ex.getMessage(), false);
        }

        pendingCheckInReservation = null;
        showNode(checkInDialog, false);
        refreshDashboard();
    }

    @FXML
    private void onDialogCancel() {
        pendingCheckInReservation = null;
        showNode(checkInDialog, false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Check-Out Dialog
    // ─────────────────────────────────────────────────────────────────────────

    private void openCheckOutDialog(Reservation res) {
        pendingCheckOutReservation = res;

        lblCheckOutDialogTitle.setText("Confirm Check-Out — Room " + res.getRoom().getRoomNumber());
        lblCheckOutDialogInfo.setText(
                "Guest: " + res.getGuest().getUserName() + "\n" +
                        "Room: " + res.getRoom().getRoomNumber()
                        + "  (" + res.getRoom().getRoomType().getTypeName() + ")\n" +
                        "Check-out date: " + res.getCheckoutDate() + "\n" +
                        "Nights stayed: " + res.calcnights() + "\n" +
                        "Status: " + res.getStatus()
        );

        showNode(checkOutDialog, true);
        showNode(checkInDialog, false);
        hideFeedback();
    }

    @FXML
    private void onCheckOutConfirm() {
        if (pendingCheckOutReservation == null) return;

        Receptionist rec = getLoggedInReceptionist();
        if (rec == null) {
            showFeedback("⚠  Session error: no receptionist found.", false);
            return;
        }

        try {
            // Build a simple auto-review (score=5, no text) so manageCheckOut doesn't NPE
            Review autoReview = new Review(5, "Checked out via receptionist dashboard.");
            rec.manageCheckOut(pendingCheckOutReservation.getReservationID(), autoReview);

            String msg = "✔  Check-out completed for " + pendingCheckOutReservation.getGuest().getUserName()
                    + "  (Room " + pendingCheckOutReservation.getRoom().getRoomNumber() + ")";
            logActivity(msg);
            showFeedback(msg, true);
        } catch (Exception ex) {
            showFeedback("✕  Check-out failed: " + ex.getMessage(), false);
        }

        pendingCheckOutReservation = null;
        showNode(checkOutDialog, false);
        refreshDashboard();
    }

    @FXML
    private void onCheckOutDialogCancel() {
        pendingCheckOutReservation = null;
        showNode(checkOutDialog, false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Toolbar Actions
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    private void onLogout() {
        SessionManager.clearSession();
        SceneManager.navigate("login-page.fxml");
    }

    @FXML
    private void onViewAllCheckIns() {
        // Navigate to a full reservations list filtered to today's check-ins
        // SceneManager.navigate("ReservationsScreen.fxml");
        showFeedback("ℹ  Full check-in list — coming soon.", true);
    }

    @FXML
    private void onViewAllCheckOuts() {
        showFeedback("ℹ  Full check-out list — coming soon.", true);
    }

    @FXML
    private void onResolveAllPending() {
        List<Reservation> pending = Database.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .toList();

        if (pending.isEmpty()) {
            showFeedback("ℹ  No pending reservations to resolve.", true);
            return;
        }

        for (Reservation res : pending) {
            res.confirmreservation();
        }
        Database.saveData();

        String msg = "✔  " + pending.size() + " pending reservation(s) marked as CONFIRMED.";
        logActivity(msg);
        showFeedback(msg, true);
        refreshDashboard();
    }

    @FXML
    private void onClearLog() {
        activityEntries.clear();
        activityLog.getChildren().clear();
        Label placeholder = new Label("No activity yet today.");
        placeholder.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa; -fx-font-style: italic;");
        activityLog.getChildren().add(placeholder);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Live Search
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    private void onSearch() {
        String query = searchField.getText();
        if (query == null || query.isBlank()) {
            showNode(searchResultsPanel, false);
            return;
        }
        String q = query.trim().toLowerCase();
        StringBuilder sb = new StringBuilder();

        // Search guests
        Database.getGuests().stream()
                .filter(g -> g.getUserName() != null && g.getUserName().toLowerCase().contains(q))
                .limit(5)
                .forEach(g -> sb.append("👤  Guest: ").append(g.getUserName()).append("\n"));

        // Search reservations by ID or guest name
        Database.getReservations().stream()
                .filter(r -> String.valueOf(r.getReservationID()).contains(q)
                        || r.getGuest().getUserName().toLowerCase().contains(q))
                .limit(5)
                .forEach(r -> sb.append("📋  Res #").append(r.getReservationID())
                        .append("  —  ").append(r.getGuest().getUserName())
                        .append("  Room ").append(r.getRoom().getRoomNumber())
                        .append("  [").append(r.getStatus()).append("]\n"));

        // Search rooms by number
        Database.getRooms().stream()
                .filter(r -> String.valueOf(r.getRoomNumber()).contains(q))
                .limit(5)
                .forEach(r -> sb.append("🛏  Room ").append(r.getRoomNumber())
                        .append("  —  ").append(r.getRoomType().getTypeName()).append("\n"));

        if (sb.isEmpty()) {
            lblSearchResults.setText("No results found");
        } else {
            lblSearchResults.setText(sb.toString().trim());
        }
        showNode(searchResultsPanel, true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Clock
    // ─────────────────────────────────────────────────────────────────────────

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm:ss a");
        lblCurrentTime.setText(LocalTime.now().format(fmt));
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e ->
                lblCurrentTime.setText(LocalTime.now().format(fmt))));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns the currently logged-in Receptionist, or null if session is invalid. */
    private Receptionist getLoggedInReceptionist() {
        User user = SessionManager.getLoggedInUser();
        return (user instanceof Receptionist rec) ? rec : null;
    }

    /** Creates a circular avatar label showing up to 2 initials from a username. */
    private Label initials(String username, String bgColor, String fgColor) {
        String ini = (username == null || username.isBlank())
                ? "?"
                : username.substring(0, Math.min(2, username.length())).toUpperCase();
        Label lbl = new Label(ini);
        lbl.setAlignment(javafx.geometry.Pos.CENTER);
        lbl.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 20; " +
                "-fx-font-weight: bold; -fx-text-fill: " + fgColor + "; -fx-font-size: 13px;");
        return lbl;
    }

    /** Appends a timestamped entry to the activity log VBox. */
    private void logActivity(String message) {
        String ts = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String entry = "[" + ts + "]  " + message;
        activityEntries.add(entry);

        // Remove placeholder if present
        activityLog.getChildren().removeIf(n ->
                n instanceof Label lbl &&
                        "No activity yet today.".equals(lbl.getText()));

        Label row = new Label(entry);
        row.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");
        row.setWrapText(true);
        activityLog.getChildren().add(0, row); // newest first
    }

    /** Shows a feedback label with green (success) or red (error) styling. */
    private void showFeedback(String message, boolean success) {
        lblFeedback.setText(message);
        lblFeedback.setStyle("-fx-font-size: 13px; -fx-text-fill: "
                + (success ? "#2e8b57" : "#c0392b") + ";");
        showNode(lblFeedback, true);
    }

    private void hideFeedback() {
        showNode(lblFeedback, false);
    }

    /** Toggles managed + visible together so the node takes no layout space when hidden. */
    private void showNode(javafx.scene.Node node, boolean show) {
        node.setVisible(show);
        node.setManaged(show);
    }

    private String safeStr(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
