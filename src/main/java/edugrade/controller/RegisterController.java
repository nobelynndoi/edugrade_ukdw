package edugrade.controller;

import edugrade.dao.UserDAO;
import edugrade.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.IOException;

public class RegisterController {

    private final Runnable onRegisterSuccess;
    private final Runnable onNavigateToLogin;
    private final UserDAO userDAO = new UserDAO();

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button registerButton;

    public RegisterController(Runnable onRegisterSuccess, Runnable onNavigateToLogin) {
        this.onRegisterSuccess = onRegisterSuccess;
        this.onNavigateToLogin = onNavigateToLogin;
    }

    public Parent createView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edugrade/register.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }

    @FXML
    private void handleRegisterMouseEntered(MouseEvent e) {
        registerButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    @FXML
    private void handleRegisterMouseExited(MouseEvent e) {
        registerButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields.");
            messageLabel.setTextFill(Color.RED);
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
            return;
        }

        User newUser = new User(username, password, name);
        boolean success = userDAO.registerUser(newUser);
        
        if (success) {
            messageLabel.setText("Registration successful!");
            messageLabel.setTextFill(Color.GREEN);
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful. Please login.", ButtonType.OK);
            alert.showAndWait();
            onRegisterSuccess.run();
        } else {
            messageLabel.setText("Registration failed. Username may exist.");
            messageLabel.setTextFill(Color.RED);
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
        }
    }

    @FXML
    private void handleLoginLink() {
        onNavigateToLogin.run();
    }
}
