package hotel.GUI.controllers;

import hotel.GUI.utils.SceneManager;
import hotel.core.Database;
import hotel.model.enums.AccountStatus;
import hotel.model.enums.Gender;
import hotel.model.enums.UserType;
import hotel.model.users.Guest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class RegistrationController {

    @FXML private TextField        usernameField;
    @FXML private PasswordField    passwordField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker       dobPicker;
    @FXML private TextField        phoneField;
    @FXML private TextField        addressField;

    @FXML private Label charCountIcon;
    @FXML private Label numberIcon;
    @FXML private Label capitalIcon;

    @FXML private Label  registerErrorLabel;
    @FXML private Button createAccountButton;

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female");

        passwordField.textProperty().addListener((obs, oldVal, newVal) ->
                validatePasswordLive(newVal));

        createAccountButton.setOnAction(this::handleCreateAccount);
    }

    private void validatePasswordLive(String password) {
        charCountIcon.setStyle(password.length() >= 8      ? "-fx-text-fill: #3fb68b;" : "-fx-text-fill: #73777f;");
        numberIcon.setStyle(password.matches(".*\\d.*")    ? "-fx-text-fill: #3fb68b;" : "-fx-text-fill: #73777f;");
        capitalIcon.setStyle(password.matches(".*[A-Z].*") ? "-fx-text-fill: #3fb68b;" : "-fx-text-fill: #73777f;");
    }

    @FXML
    void handleCreateAccount(ActionEvent event) {
        clearError();

        String username  = usernameField.getText().trim();
        String password  = passwordField.getText();
        String genderStr = genderComboBox.getValue();
        LocalDate dob    = dobPicker.getValue();
        String phone     = phoneField.getText().trim();
        String address   = addressField.getText().trim();

        // All fields required
        if (username.isEmpty() || password.isEmpty() || genderStr == null
                || dob == null || phone.isEmpty() || address.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        // Duplicate username
        for (Guest g : Database.getGuests()) {
            if (g.getUserName().equalsIgnoreCase(username)) {
                showError("Username already taken. Please choose another.");
                return;
            }
        }

        // Password rules
        if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[A-Z].*")) {
            showError("Password must be 8+ characters with a number and a capital letter.");
            return;
        }

        // Phone
        if (phone.length() != 11 || !phone.matches("\\d+")) {
            showError("Phone number must be exactly 11 digits.");
            return;
        }

        // Build and save guest
        Guest newGuest = new Guest();
        newGuest.setUserName(username);
        newGuest.setPassword(password);
        newGuest.setTypeofuser(UserType.GUEST);
        newGuest.setTheGender(genderStr.equalsIgnoreCase("Male") ? Gender.MALE : Gender.FEMALE);
        newGuest.setAccountStatus(AccountStatus.ACTIVE);
        newGuest.setDateOfbirth(dob);
        newGuest.setPhoneNumber(phone);
        newGuest.setAddress(address);
        newGuest.setBalance(0.0);
        newGuest.setFailedLoginAttempts(0);
        newGuest.setUniqueId(Database.getGuests().isEmpty()? 1000: Database.getGuests().get(Database.getGuests().size() - 1).getUniqueId() + 1);

        Database.getGuests().add(newGuest);
        Database.saveData();

        showSuccess("Welcome, " + username + "! Your ID is " + newGuest.getUniqueId() + ". Redirecting...");

        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.8));
        pause.setOnFinished(e -> SceneManager.navigate("login-page.fxml"));
        pause.play();
    }

    @FXML
    void handleSignIn(ActionEvent event) {
        SceneManager.navigate("login-page.fxml");
    }

    private void showError(String message) {
        if (registerErrorLabel == null) return;
        registerErrorLabel.setText(message);
        registerErrorLabel.setStyle(
                "-fx-text-fill: #b9120f; -fx-background-color: #fdecea; " +
                "-fx-background-radius: 4; -fx-padding: 8 12 8 12;");
        registerErrorLabel.setVisible(true);
        registerErrorLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        if (registerErrorLabel == null) return;
        registerErrorLabel.setText(message);
        registerErrorLabel.setStyle(
                "-fx-text-fill: #1a6b3a; -fx-background-color: #e6f4ec; " +
                "-fx-background-radius: 4; -fx-padding: 8 12 8 12;");
        registerErrorLabel.setVisible(true);
        registerErrorLabel.setManaged(true);
    }

    private void clearError() {
        if (registerErrorLabel == null) return;
        registerErrorLabel.setVisible(false);
        registerErrorLabel.setManaged(false);
    }
}