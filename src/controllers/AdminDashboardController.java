package controllers;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import models.Project;
import repository.ProjectRepository;
import utils.SceneController;

public class AdminDashboardController {

    @FXML private ListView<Project> projectsList;
    @FXML private TextField projectNameField;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextArea descArea;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        setupListView();
        refresh();
    }

    /** Setup ListView with beautifully styled light-themed project cards with smooth hover animation */
    private void setupListView() {

        projectsList.setCellFactory(lv -> new ListCell<>() {

            private final VBox card = new VBox();
            private final Label nameLabel = new Label();
            private final Label deadlineLabel = new Label();
            private final Label descLabel = new Label();
            private final HBox container = new HBox();

            private final ScaleTransition hoverTransition = new ScaleTransition(Duration.millis(150), card);

            {
                // Layout
                card.setSpacing(6);
                card.setPadding(new Insets(12));
                card.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), new CornerRadii(12), Insets.EMPTY)));
                card.setBorder(new Border(new BorderStroke(Color.web("#d9e2ef"),
                        BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1))));

                // Titles
                nameLabel.setFont(Font.font("Segoe UI Semibold", 16));
                nameLabel.setTextFill(Color.web("#0a1a2f")); // Dark title

                deadlineLabel.setFont(Font.font("Segoe UI", 13));
                deadlineLabel.setTextFill(Color.web("#4a5b6c")); // Secondary text

                descLabel.setFont(Font.font("Segoe UI", 14));
                descLabel.setTextFill(Color.web("#4a5b6c")); // Description text
                descLabel.setWrapText(true);

                card.getChildren().addAll(nameLabel, deadlineLabel, descLabel);
                container.getChildren().add(card);
                container.setPadding(new Insets(5));

                // Base card style: soft shadow
                card.setStyle(
                        "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #d9e2ef;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 2);"
                );

                // Configure hover animation
                hoverTransition.setFromX(1.0);
                hoverTransition.setFromY(1.0);
                hoverTransition.setToX(1.03);
                hoverTransition.setToY(1.03);
            }

            @Override
            protected void updateItem(Project p, boolean empty) {
                super.updateItem(p, empty);

                if (empty || p == null) {
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(p.getName());
                deadlineLabel.setText("Deadline: " + p.getDeadline());
                descLabel.setText(p.getDescription());

                // Hover effect with smooth scaling and color change
                this.hoverProperty().addListener((obs, oldVal, hovered) -> {
                    if (hovered) {
                        hoverTransition.setRate(1);
                        hoverTransition.playFromStart();
                        card.setStyle(
                                "-fx-background-color: #f0f4ff;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #4d79ff;" +
                                "-fx-border-width: 1.5;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,122,255,0.15), 8, 0, 0, 4);"
                        );
                    } else {
                        hoverTransition.setRate(-1);
                        hoverTransition.playFrom(Duration.millis(150));
                        card.setStyle(
                                "-fx-background-color: #FFFFFF;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #d9e2ef;" +
                                "-fx-border-width: 1;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 2);"
                        );
                    }
                });

                // Selected effect
                projectsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                    if (p.equals(newSel)) {
                        card.setStyle(
                                "-fx-background-color: #1a73ff;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #1a73ff;" +
                                "-fx-border-width: 1;" +
                                "-fx-effect: dropshadow(gaussian, rgba(26,115,255,0.3), 8, 0, 0, 4);"
                        );
                        nameLabel.setTextFill(Color.WHITE);
                        deadlineLabel.setTextFill(Color.web("#DCE6FF"));
                        descLabel.setTextFill(Color.web("#DCE6FF"));
                    } else if (!this.isHover()) {
                        card.setStyle(
                                "-fx-background-color: #FFFFFF;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #d9e2ef;" +
                                "-fx-border-width: 1;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 2);"
                        );
                        nameLabel.setTextFill(Color.web("#0a1a2f"));
                        deadlineLabel.setTextFill(Color.web("#4a5b6c"));
                        descLabel.setTextFill(Color.web("#4a5b6c"));
                    }
                });

                setGraphic(container);
            }
        });

        // Double-click popup for project details
        projectsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Project selected = projectsList.getSelectionModel().getSelectedItem();
                if (selected != null) showProjectDetails(selected);
            }
        });
    }

    /** Show project details */
    private void showProjectDetails(Project p) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Project Details");
        alert.setHeaderText(p.getName());
        alert.setContentText(
                "Deadline: " + p.getDeadline() +
                "\n\nDescription:\n" + p.getDescription()
        );
        alert.showAndWait();
    }

    /** Refresh ListView items */
    private void refresh() {
        projectsList.getItems().setAll(ProjectRepository.getAll());
    }

    /** Create a new project */
    @FXML
    private void onCreateProject() {
        String name = projectNameField.getText().trim();
        String desc = descArea.getText().trim();

        if (name.isEmpty() || deadlinePicker.getValue() == null) {
            statusLabel.setText("Fill name and deadline");
            return;
        }

        Project p = new Project(
                ProjectRepository.generateId(),
                name,
                desc,
                deadlinePicker.getValue().toString(),
                null,
                null
        );

        ProjectRepository.add(p);
        ProjectRepository.loadAll(); // 🔥 ensure memory sync
        refresh();

        statusLabel.setText("Project created successfully");
    }

    /** Delete selected project */
    @FXML
    private void onDeleteProject() {
        Project p = projectsList.getSelectionModel().getSelectedItem();

        if (p == null) {
            statusLabel.setText("Select a project first!");
            return;
        }

        boolean removed = ProjectRepository.remove(p);
        if (removed) {
            refresh();
            statusLabel.setText("Project removed successfully");
        } else {
            statusLabel.setText("Failed to remove project");
        }
    }

    /** Back button action */
    @FXML
    private void onBackToLogin() {
        SceneController.switchTo("Login.fxml");
    }
}
