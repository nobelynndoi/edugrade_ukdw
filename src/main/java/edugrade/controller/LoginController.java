package edugrade.controller;

import edugrade.app.AppSession;
import edugrade.dao.UserDAO;
import edugrade.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class LoginController {

    private final Runnable onLoginSuccess;
    private final Runnable onNavigateToRegister;
    private final UserDAO userDAO = new UserDAO();

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    public LoginController(Runnable onLoginSuccess, Runnable onNavigateToRegister) {
        this.onLoginSuccess = onLoginSuccess;
        this.onNavigateToRegister = onNavigateToRegister;
    }

    public Parent createView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edugrade/login.fxml"));
            loader.setController(this);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }

    @FXML
    private void handleLoginMouseEntered(MouseEvent e) {
        loginButton.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    @FXML
    private void handleLoginMouseExited(MouseEvent e) {
        loginButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill all fields.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        User user = userDAO.authenticateUser(username, password);
        if (user != null) {
            AppSession.setCurrentUser(user);
            onLoginSuccess.run();
        } else {
            errorLabel.setText("Invalid username or password.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    @FXML
    private void handleRegisterLink() {
        onNavigateToRegister.run();
    }
}
