package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Client;
import models.Project;
import repository.ProjectRepository;
import services.RiskAISuggester;
import utils.SceneController;

import java.util.List;
import java.util.stream.Collectors;

public class ClientDashboardController {
    
    @FXML private ListView<Project> myProjectsList;
    @FXML private Label riskLabel;
    @FXML private Label statusLabel;

    private Client loggedInClient;

    @FXML
    public void initialize() {
        // Get logged-in client
        loggedInClient = (Client) LoginController.getLoggedInUser();

        if (loggedInClient == null) {
            if (statusLabel != null) {
                statusLabel.setText("ERROR: No client session found.");
            }
            return;
        }

        // Load projects assigned to this client
        List<Project> clientProjects = ProjectRepository.getAll().stream()
                .filter(p -> loggedInClient.getId().equals(p.getClientId()))
                .collect(Collectors.toList());

        // If no projects assigned, show all projects
        if (clientProjects.isEmpty()) {
            clientProjects = ProjectRepository.getAll();
        }

        myProjectsList.setItems(FXCollections.observableArrayList(clientProjects));
    }

    @FXML
    private void onSelectProject() {
        Project p = myProjectsList.getSelectionModel().getSelectedItem();
        if (p == null) return;

        double risk = RiskAISuggester.computeRisk(p) * 100;

        riskLabel.setText(String.format("%.0f%%", risk));

    // Reset styles
        riskLabel.getStyleClass().removeAll("risk-low", "risk-medium", "risk-high");

    // Apply risk level style
        if (risk < 35) {
            riskLabel.getStyleClass().add("risk-low");
        } else if (risk < 70) {
            riskLabel.getStyleClass().add("risk-medium");
        } else {
            riskLabel.getStyleClass().add("risk-high");
    }
}


    @FXML
    private void onBackToLogin() {
        SceneController.switchTo("Login.fxml");
    }
}
