package utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;

public class SceneController {

    private static Stage mainStage;

    /** Register the primary stage for global scene switching */
    public static void setMainStage(Stage stage) {
        mainStage = stage;

        if (mainStage != null) {
            // Force maximize immediately at start
            maximizeStage(mainStage);
        }
    }

    /** Switch scenes safely with full error handling and maximize */
    public static void switchTo(String fxmlFile) {
        try {
            if (mainStage == null) {
                System.err.println("ERROR: mainStage is NULL. Call SceneController.setMainStage() first!");
                return;
            }

            // Load FXML
            URL fxmlPath = SceneController.class.getResource("/fxml/" + fxmlFile);
            if (fxmlPath == null) {
                System.err.println("ERROR: FXML NOT FOUND → /fxml/" + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlPath);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Apply CSS
            try {
                URL cssPath = SceneController.class.getResource("/css/style.css");
                if (cssPath != null) {
                    scene.getStylesheets().add(cssPath.toExternalForm());
                    System.out.println("style.css applied");
                }
            } catch (Exception e) {
                System.out.println("Failed to load style.css: " + e.getMessage());
            }

            // Set scene
            mainStage.setScene(scene);

            // Maximize stage robustly
            maximizeStage(mainStage);

            mainStage.show();

            System.out.println("Switched to: " + fxmlFile);

        } catch (Exception e) {
            System.err.println("Scene switching error for file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    /** Force stage to cover full screen (maximized) */
    private static void maximizeStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setResizable(true);
        stage.setMaximized(true);
    }
}
