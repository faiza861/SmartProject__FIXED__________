package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Circle;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.animation.*;
import javafx.util.Duration;
import utils.SceneController;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML private ImageView logo;
    @FXML private Label devText;
    @FXML private StackPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLogo();
        makeLogoCircular();
        setupLogoHover();
        runAnimations();
    }

    /** Load logo image safely */
    private void loadLogo() {
        try {
            URL url = getClass().getResource("/images/logo.png");
            if (url != null) {
                logo.setImage(new Image(url.toExternalForm()));
            } else {
                System.err.println("Logo not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Make the logo circular with smooth edges */
    private void makeLogoCircular() {
        double radius = Math.min(logo.getFitWidth(), logo.getFitHeight()) / 2;
        Circle clip = new Circle(radius, radius, radius);
        logo.setClip(clip);

        // Bake clip for smooth edges
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = logo.snapshot(params, null);
        logo.setClip(null);
        logo.setImage(snapshot);
    }

    /** Slight interactive scale on hover */
    private void setupLogoHover() {
        logo.setOnMouseEntered(e -> {
            logo.setScaleX(1.05);
            logo.setScaleY(1.05);
        });
        logo.setOnMouseExited(e -> {
            logo.setScaleX(1.0);
            logo.setScaleY(1.0);
        });
    }

    /** Run all splash animations */
    private void runAnimations() {

        // Logo fade + scale
        logo.setOpacity(0);
        logo.setScaleX(0.7);
        logo.setScaleY(0.7);

        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(1.8), logo);
        fadeLogo.setFromValue(0);
        fadeLogo.setToValue(1);

        ScaleTransition scaleLogo = new ScaleTransition(Duration.seconds(1.8), logo);
        scaleLogo.setFromX(0.7);
        scaleLogo.setFromY(0.7);
        scaleLogo.setToX(1.0);
        scaleLogo.setToY(1.0);
        scaleLogo.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition logoTransition = new ParallelTransition(fadeLogo, scaleLogo);
        logoTransition.play();

        // Glow effect on developer text
        Glow glow = new Glow(0.0);
        devText.setEffect(glow);
        Timeline glowEffect = new Timeline(
                new KeyFrame(Duration.seconds(0.0), e -> glow.setLevel(0.0)),
                new KeyFrame(Duration.seconds(1.2), e -> glow.setLevel(0.8)),
                new KeyFrame(Duration.seconds(2.0), e -> glow.setLevel(0.4))
        );
        glowEffect.setCycleCount(Animation.INDEFINITE);
        glowEffect.setAutoReverse(true);
        glowEffect.play();

        // Animated gradient background
        Timeline bgAnim = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> root.setStyle("-fx-background-color: linear-gradient(to bottom, #061a2d, #0a3147);")),
                new KeyFrame(Duration.seconds(4), e -> root.setStyle("-fx-background-color: linear-gradient(to bottom, #0a3147, #061a2d);"))
        );
        bgAnim.setAutoReverse(true);
        bgAnim.setCycleCount(Animation.INDEFINITE);
        bgAnim.play();

        // Auto redirect to Login
        PauseTransition delay = new PauseTransition(Duration.seconds(3.5));
        delay.setOnFinished(e -> SceneController.switchTo("Login.fxml"));
        delay.play();
    }
}
