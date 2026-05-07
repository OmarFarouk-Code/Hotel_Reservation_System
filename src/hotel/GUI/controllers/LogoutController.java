package hotel.GUI.controllers;

import hotel.GUI.utils.SceneManager;
import hotel.GUI.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LogoutController {

    @FXML private Button cancelButton;
    @FXML private Button logoutButton;

    @FXML
    private void handleCancel() {
        // Navigate back to the appropriate dashboard based on the current user's role
        if (!SessionManager.isLoggedIn()) {
            SceneManager.navigate("login-page.fxml");
            return;
        }

        String role = SessionManager.getLoggedInUser().getTypeofuser().name();
        switch (role) {
            case "ADMIN"        -> SceneManager.navigate("AdminDashboard.fxml");
            case "RECEPTIONIST" -> SceneManager.navigate("ReceptionistDashboard.fxml");
            default             -> SceneManager.navigate("GuestDashboard.fxml");
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        SceneManager.navigate("login-page.fxml");
    }
}