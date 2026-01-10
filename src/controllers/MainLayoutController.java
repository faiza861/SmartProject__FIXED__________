package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private ImageView logo;
    @FXML private StackPane contentPane;

    @FXML
    public void initialize() {
        loadLogo();
        showLogin();
    }

    private void loadLogo() {
        try {
            var url = getClass().getResource("/images/logo.png");
            if (url != null) {
                logo.setImage(new Image(url.toExternalForm()));
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load logo");
        }
    }

    @FXML
    private void showLogin() {
        loadForm("Login.fxml");
    }

    @FXML
    private void showRegister() {
        loadForm("Register.fxml");
    }

    private void loadForm(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent form = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(form);
        } catch (Exception e) {
            System.err.println("❌ Failed to load: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
