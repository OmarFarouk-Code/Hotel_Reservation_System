package

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    // 1. Link the Java variables to the Scene Builder fx:id's
    @FXML private Button btnGuest;
    @FXML private Button btnReceptionist;
    @FXML private Button btnAdmin;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnSignIn;

    // Store the currently selected role
    private String selectedRole = "Guest"; 

    // 2. The initialize method runs automatically when the screen loads
    @FXML
    public void initialize() {
        // Wire up the click events for the role tabs
        btnGuest.setOnAction(event -> switchRole("Guest", btnGuest));
        btnReceptionist.setOnAction(event -> switchRole("Receptionist", btnReceptionist));
        btnAdmin.setOnAction(event -> switchRole("Admin", btnAdmin));

        // Wire up the sign-in button
        btnSignIn.setOnAction(event -> onSignIn());

        // Set the initial visual state (Guest tab active)
        switchRole("Guest", btnGuest);
    }

    // 3. Handle the visual tab switching
    private void switchRole(String role, Button clickedButton) {
        this.selectedRole = role;

        // Remove the active class from ALL buttons first
        btnGuest.getStyleClass().remove("role-tab-active");
        btnReceptionist.getStyleClass().remove("role-tab-active");
        btnAdmin.getStyleClass().remove("role-tab-active");

        // Add the active class ONLY to the clicked button
        if (!clickedButton.getStyleClass().contains("role-tab-active")) {
            clickedButton.getStyleClass().add("role-tab-active");
        }
    }

    // 4. Handle the actual login action
    private void onSignIn() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // For now, print to the console to prove it works
        System.out.println("Attempting login...");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Role: " + selectedRole);

        // Later, you will link this to your User classes and Database.
        // Once validated, you'll use the SceneManager to navigate:
        if (selectedRole.equals("Guest")) {
            System.out.println("-> Would navigate to GuestDashboard.fxml");
        } else if (selectedRole.equals("Receptionist")) {
            System.out.println("-> Would navigate to ReceptionistDashboard.fxml");
        } else if (selectedRole.equals("Admin")) {
            System.out.println("-> Would navigate to AdminDashboard.fxml");
        }
    }
}