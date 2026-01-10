package controllers;

import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import models.Member;
import models.Project;
import models.Task;
import repository.ProjectRepository;
import utils.SceneController;

import java.util.HashMap;
import java.util.Map;

public class MemberDashboardController {

    @FXML private ListView<Task> tasksList;
    @FXML private TextField progressField;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressValueLabel;
    @FXML private Button updateProgressBtn;
    @FXML private Label statusLabel;
    @FXML private Label welcomeLabel;

    private Member loggedInMember;
    private ObservableList<Task> myTasks = FXCollections.observableArrayList();

    // 🔧 FIX: Track parent project for each task
    private final Map<Task, Project> taskProjectMap = new HashMap<>();

    @FXML
    public void initialize() {
        loggedInMember = (Member) LoginController.getLoggedInUser();
        if (loggedInMember == null) {
            statusLabel.setText("Session expired. Please login again.");
            updateProgressBtn.setDisable(true);
            return;
        }

        welcomeLabel.setText("Welcome back, " + loggedInMember.getName());

        setupListView();
        loadAssignedTasks();

        updateProgressBtn.setOnAction(e -> onUpdateProgress());

        progressField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) onUpdateProgress();
        });
    }

    /* ---------------------- ListView Setup ---------------------- */
    private void setupListView() {
        tasksList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tasksList.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
            Platform.runLater(() -> {
                if (newTask != null) updateProgressUI(newTask.getProgress());
                else clearProgressUI();
            });
        });
        tasksList.setItems(myTasks);
    }

    private void loadAssignedTasks() {
        myTasks.clear();
        taskProjectMap.clear();

        for (Project p : ProjectRepository.getAll()) {
            for (Task t : p.getTasks()) {
                if (loggedInMember.getId().equals(t.getAssignedMemberId())) {
                    if (!myTasks.contains(t)) {
                        myTasks.add(t);
                        taskProjectMap.put(t, p); // 🔧 FIX
                    }
                }
            }
        }
    }

    /* ---------------------- Progress UI ---------------------- */
    private void updateProgressUI(double value) {
        int intValue = (int) Math.round(value);
        progressField.setText(String.valueOf(intValue));
        progressBar.setProgress(value / 100.0);
        progressValueLabel.setText(intValue + "%");
    }

    private void clearProgressUI() {
        progressField.clear();
        progressBar.setProgress(0);
        progressValueLabel.setText("0%");
    }

    /* ---------------------- Progress Controls ---------------------- */
    @FXML
    private void increaseProgress() { adjustProgress(5); }

    @FXML
    private void decreaseProgress() { adjustProgress(-5); }

    private void adjustProgress(double delta) {
        Task selectedTask = tasksList.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showStatus("Select a task first!", false);
            return;
        }

        try {
            double current = Double.parseDouble(progressField.getText());
            double updated = Math.min(100, Math.max(0, current + delta));

            selectedTask.setProgress(updated);

            // 🔧 FIX: Update real parent project directly
            Project parent = taskProjectMap.get(selectedTask);
            if (parent != null) parent.updateProgress();

            ProjectRepository.saveAll();
            tasksList.refresh();
            updateProgressUI(updated);
            showStatus("Progress updated ✔", true);

        } catch (NumberFormatException e) {
            showStatus("Invalid number entered!", false);
        }
    }

    /* ---------------------- Update Progress Button ---------------------- */
    @FXML
    private void onUpdateProgress() {
        Task selectedTask = tasksList.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showStatus("Select a task first!", false);
            return;
        }

        try {
            double progress = Double.parseDouble(progressField.getText());
            if (progress < 0 || progress > 100) {
                showStatus("Progress must be between 0 and 100", false);
                return;
            }

            selectedTask.setProgress(progress);

            // 🔧 FIX: Update real parent project directly
            Project parent = taskProjectMap.get(selectedTask);
            if (parent != null) parent.updateProgress();

            ProjectRepository.saveAll();
            tasksList.refresh();
            updateProgressUI(progress);
            showStatus("Progress updated successfully ✔", true);

        } catch (NumberFormatException e) {
            showStatus("Enter a valid number", false);
        }
    }

    /* ---------------------- Status Feedback ---------------------- */
    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("success-label", "error-label");
        statusLabel.getStyleClass().add(success ? "success-label" : "error-label");
        FadeTransition ft = new FadeTransition(Duration.millis(400), statusLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /* ---------------------- Scene Switching ---------------------- */
    @FXML
    private void onBackToLogin() {
        SceneController.switchTo("Login.fxml");
    }
}
