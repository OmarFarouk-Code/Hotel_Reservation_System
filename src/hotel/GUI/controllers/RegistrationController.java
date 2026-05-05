import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import hotel.GUI.utils.SceneManager;
import javafx.event.ActionEvent;

public class RegistrationController {

    // --- Input Fields ---
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> genderComboBox; 
    @FXML private DatePicker dobPicker;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;

    // --- Validation Icons ---
    @FXML private Label charCountIcon;
    @FXML private Label numberIcon;
    @FXML private Label capitalIcon;

    // --- Action Controls ---
    @FXML private Button createAccountButton;
    @FXML private Hyperlink signInLink;

    @FXML
    public void initialize() {
        // Populate your gender dropdown here
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        
        // You can add a listener to the password field here later 
        // to dynamically update the checkmarks as the user types!
    }

    @FXML
    void handleCreateAccount(ActionEvent event) {
        // Grab the text to send to your Singleton database:
        // String username = usernameField.getText();
        System.out.println("Create account clicked!");
    }

    @FXML
    void handleSignIn(ActionEvent event) {
        // Logic to switch scenes back to the login screen
        System.out.println("Switching to Sign In screen...");
        SceneManager.navigate("login-page.fxml");
    }
}