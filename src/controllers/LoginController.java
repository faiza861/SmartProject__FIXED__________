package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import models.User;
import services.AuthService;
import utils.SceneController;

public class LoginController {

    // ================== FXML FIELDS ==================

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView logo;

    // ================== SESSION ==================

    /** Global session for logged-in user */
    private static User loggedInUser;

    /** Getter for dashboards */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    // ================== INITIALIZATION ==================

    @FXML
    public void initialize() {
        loadLogo();
        statusLabel.setText("");
    }

    /** Load logo safely and make it circular */
    private void loadLogo() {
        try {
            var url = getClass().getResource("/images/logo.png");
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                logo.setImage(image);

                // Make logo circular (matches modern UI)
                double radius = 60;
                Circle clip = new Circle(radius, radius, radius);
                logo.setClip(clip);

            } else {
                System.out.println("⚠️ Logo not found: /images/logo.png");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load logo: " + e.getMessage());
        }
    }

    // ================== LOGIN ==================

    @FXML
    private void onLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // 🔹 Validation
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        // 🔹 Authenticate user
        User user = AuthService.login(email, password);

        if (user == null) {
            statusLabel.setText("Invalid email or password.");
            return;
        }

        // 🔹 Save session
        loggedInUser = user;
        statusLabel.setText("");

        // 🔹 Route based on role
        try {
            switch (user.getRole().toUpperCase()) {

                case "ADMIN" ->
                        SceneController.switchTo("AdminDashboard.fxml");

                case "MANAGER" ->
                        SceneController.switchTo("ManagerDashboard.fxml");

                case "MEMBER" ->
                        SceneController.switchTo("MemberDashboard.fxml");

                case "CLIENT" ->
                        SceneController.switchTo("ClientDashboard.fxml");

                default ->
                        statusLabel.setText("Unknown role: " + user.getRole());
            }
        } catch (Exception e) {
            statusLabel.setText("Unable to load dashboard.");
            e.printStackTrace();
        }
    }

    // ================== REGISTER ==================

    @FXML
    private void onRegister() {
        try {
            SceneController.switchTo("Register.fxml");
        } catch (Exception e) {
            statusLabel.setText("Unable to open registration page.");
            e.printStackTrace();
        }
    }
}
