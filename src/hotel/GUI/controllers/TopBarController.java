package hotel.GUI.controllers;

import java.util.ArrayList;
import java.util.List;
import hotel.GUI.utils.SessionManager;
import hotel.core.Database;
import hotel.model.entities.Room;
import hotel.model.users.Guest;
import hotel.model.users.User;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class TopBarController {

    @FXML private Label     lblUsername;
    @FXML private TextField txtSearch;
    @FXML private ImageView imgBell;
    @FXML private ImageView imgSettings;
    @FXML private ImageView imgHelp;
    @FXML private ImageView imgAvatar;
    @FXML private Label     lblPageTitle;
    @FXML private Label     lblPageSubtitle;

    // Added: ContextMenu to act as a dropdown for search results
    private ContextMenu searchPopup; 

    @FXML
    public void initialize() {
        refresh();
        
        // Initialize the dropdown popup
        searchPopup = new ContextMenu();
        searchPopup.setStyle("-fx-max-width: 300px;");

        if (imgBell     != null) imgBell.setOnMouseClicked(e     -> onBellClicked());
        if (imgSettings != null) imgSettings.setOnMouseClicked(e -> showComingSoon("Settings"));
        if (imgHelp     != null) imgHelp.setOnMouseClicked(e     -> showComingSoon("Help Center"));
        if (imgAvatar   != null) imgAvatar.setOnMouseClicked(e   -> showComingSoon("User Profile"));

        // Live Search Listener
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldVal, newVal) -> onSearchChanged(newVal));
            // Hide menu if focus is lost
            txtSearch.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
                if (!newFocus) searchPopup.hide();
            });
        }
    }

    public void refresh() {
        User user = SessionManager.getLoggedInUser();
        if (user != null && lblUsername != null) {
            lblUsername.setText(user.getUserName());
        } else if (lblUsername != null) {
            lblUsername.setText("Guest");
        }
    }

    public void setPageTitle(String title, String subtitle) {
        if (lblPageTitle    != null) lblPageTitle.setText(title);
        if (lblPageSubtitle != null) lblPageSubtitle.setText(subtitle);
    }

    // --- Upgraded Search Flow ---
    private void onSearchChanged(String query) {
        searchPopup.getItems().clear();

        if (query == null || query.isBlank()) {
            searchPopup.hide();
            return;
        }

        String q = query.trim().toLowerCase();
        List<MenuItem> items = new ArrayList<>();

        for (Guest g : Database.getGuests()) {
            if (g.getUserName() != null && g.getUserName().toLowerCase().contains(q)) {
                items.add(new MenuItem("👤 " + g.getUserName() + " (Guest)"));
                if (items.size() >= 6) break;
            }
        }
        for (Room r : Database.getRooms()) {
            if (String.valueOf(r.getRoomNumber()).contains(q) || r.getRoomType().getTypeName().toLowerCase().contains(q)) {
                items.add(new MenuItem("🛏 Room " + r.getRoomNumber() + " — " + r.getRoomType().getTypeName()));
                if (items.size() >= 6) break;
            }
        }

        if (items.isEmpty()) {
            MenuItem noMatch = new MenuItem("No results found...");
            noMatch.setDisable(true);
            searchPopup.getItems().add(noMatch);
        } else {
            searchPopup.getItems().addAll(items);
        }

        // Show the dropdown directly under the search bar
        if (!searchPopup.isShowing()) {
            searchPopup.show(txtSearch, Side.BOTTOM, 0, 5);
        }
    }

    // --- Upgraded Notifications ---
    private void onBellClicked() {
        User user = SessionManager.getLoggedInUser();
        if (user == null) return;

        int count = 0;
        String message = "";
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;

        switch (user.getTypeofuser()) {
            case GUEST -> {
                String username = user.getUserName();
                count = (int) Database.getInvoices().stream()
                        .filter(inv -> !inv.isPaid() && inv.getReservation().getGuest().getUserName().equals(username))
                        .count();
                if (count > 0) {
                    message = "You have " + count + " unpaid invoice(s).\nPlease go to reservations to settle your balance.";
                    alertType = Alert.AlertType.WARNING;
                } else {
                    message = "You are all caught up! No unpaid invoices.";
                }
            }
            case RECEPTIONIST -> {
                count = (int) Database.getReservations().stream().filter(r -> r.getStatus().name().equals("PENDING")).count();
                message = count == 0 ? "No pending check-ins at the moment." : count + " reservation(s) are awaiting front-desk check-in.";
            }
            case ADMIN -> {
                long cancelled = Database.getReservations().stream().filter(r -> r.getStatus().name().equals("CANCELLED")).count();
                message = "System Overview:\nTotal cancelled reservations: " + cancelled;
            }
        }

        Alert alert = new Alert(alertType);
        alert.setTitle("Notifications");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Unavailable");
        alert.setHeaderText(null);
        alert.setContentText(feature + " is currently under development.");
        alert.showAndWait();
    }
}
