package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import services.AuthService;
import utils.SceneController;

public class RegisterController {

    // ================== FXML FIELDS ==================

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label statusLabel;

    // ================== INITIALIZATION ==================

    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(
                "ADMIN",
                "MANAGER",
                "MEMBER",
                "CLIENT"
        );
        roleComboBox.setValue("MEMBER");
        statusLabel.setText("");
    }

    // ================== REGISTER ==================

    @FXML
    private void onRegister() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        // 🔹 Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!email.contains("@")) {
            showError("Please enter a valid email.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        // 🔹 Register user
        boolean success = AuthService.register(name, email, password, role);

        if (success) {
            showSuccess("Registration successful! Please login.");
            clearFields();
        } else {
            showError("Email already exists.");
        }
    }

    // ================== NAVIGATION ==================

    @FXML
    private void onBackToLogin() {
        try {
            SceneController.switchTo("Login.fxml");
        } catch (Exception e) {
            showError("Unable to open login page.");
            e.printStackTrace();
        }
    }

    // ================== HELPERS ==================

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #dc2626;");
        statusLabel.setText(message);
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #16a34a;");
        statusLabel.setText(message);
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("MEMBER");
    }
}
