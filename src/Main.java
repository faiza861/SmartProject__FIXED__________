import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SceneController;
import repository.UserRepository;
import repository.ProjectRepository;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            // Register main stage for SceneController
            SceneController.setMainStage(primaryStage);

            // --- LOAD REPOSITORIES BEFORE ANY DASHBOARD IS OPENED ---
            // Load synchronously to prevent controllers from getting null data
            try {
                UserRepository.loadAll();
                ProjectRepository.loadAll();
                System.out.println("User & Project JSON loaded successfully!");
            } catch (Exception e) {
                System.err.println("ERROR loading repositories!");
                e.printStackTrace();
            }

            // --- LOAD SPLASH SCREEN ---
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Apply CSS safely (check exists)
            try {
                String css = getClass().getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(css);
                System.out.println("style.css loaded");
            } catch (Exception e) {
                System.out.println("style.css NOT FOUND");
            }

            primaryStage.setTitle("Smart Project Management System");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Fatal error: Unable to load main UI");
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}