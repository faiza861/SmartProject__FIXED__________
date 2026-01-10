package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.*;
import repository.ProjectRepository;
import repository.UserRepository;
import services.RiskAISuggester;
import utils.SceneController;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerDashboardController {

    @FXML private ListView<Project> projectsList;
    @FXML private ListView<Task> tasksList;
    @FXML private TextArea suggestionsArea;
    @FXML private ComboBox<Member> memberComboBox;
    @FXML private TextField taskDescField;
    @FXML private TextField progressField;
    @FXML private Button assignTaskBtn;
    @FXML private Button updateProgressBtn;
    @FXML private Button backBtn;
    @FXML private TextField searchProjectField;
    @FXML private Label statusLabel;

    @FXML private Label projectsTitleLabel;
    @FXML private Label tasksTitleLabel;
    @FXML private Label suggestionsTitleLabel;

    private Manager loggedInManager;
    private List<Project> allProjects;
    private final Label tasksPlaceholderLabel =
            new Label("No tasks assigned yet. Select a project or assign a task.");

    @FXML
    public void initialize() {

        ProjectRepository.loadAll();
        UserRepository.loadAll();

        projectsTitleLabel.setFont(Font.font("Segoe UI Semibold", 18));
        tasksTitleLabel.setFont(Font.font("Segoe UI Semibold", 18));
        suggestionsTitleLabel.setFont(Font.font("Segoe UI Semibold", 18));
        tasksPlaceholderLabel.setFont(Font.font("Segoe UI", 14));
        tasksPlaceholderLabel.setWrapText(true);
        tasksList.setPlaceholder(tasksPlaceholderLabel);

        loggedInManager = (Manager) LoginController.getLoggedInUser();
        if (loggedInManager == null) {
            statusLabel.setText("ERROR: No manager session found.");
            return;
        }

        allProjects = ProjectRepository.getAll().stream()
                .filter(p -> p.getManagerId() == null || loggedInManager.getId().equals(p.getManagerId()))
                .collect(Collectors.toList());
        projectsList.setItems(FXCollections.observableArrayList(allProjects));

        memberComboBox.setItems(FXCollections.observableArrayList(UserRepository.getAllMembers()));
        projectsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        setupProjectListView();
        setupTaskListView();

        projectsList.getSelectionModel().selectedItemProperty().addListener((obs, oldProj, newProj) -> {
            if (newProj != null) {
                Project fresh = ProjectRepository.findById(newProj.getId());
                loadProjectDetails(fresh);
            } else {
                loadProjectDetails(null);
            }
        });

        searchProjectField.textProperty().addListener((obs, oldVal, newVal) -> filterProjects(newVal));
        suggestionsArea.setWrapText(true);
    }

    private void loadProjectDetails(Project project) {
        tasksList.getItems().clear();
        if (project == null) {
            suggestionsArea.clear();
            progressField.clear();
            return;
        }

        Project freshProject = ProjectRepository.findById(project.getId());
        if (freshProject == null) return;

        tasksList.setItems(FXCollections.observableArrayList(freshProject.getTasks()));
        loadAISuggestions(freshProject);
        progressField.setText(String.valueOf((int) freshProject.getProgress()));
        statusLabel.setText("");
    }

    private void filterProjects(String query) {
        if (query == null || query.isBlank()) {
            projectsList.setItems(FXCollections.observableArrayList(allProjects));
        } else {
            String lower = query.toLowerCase();
            List<Project> filtered = allProjects.stream()
                    .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
            projectsList.setItems(FXCollections.observableArrayList(filtered));
        }
        projectsList.getSelectionModel().clearSelection();
        tasksList.getItems().clear();
        suggestionsArea.clear();
    }

    private void setupProjectListView() {
        projectsList.setCellFactory(lv -> new ListCell<>() {
            private final VBox container = new VBox(5);
            private final Label name = new Label();
            private final Label deadline = new Label();
            private final ProgressBar progressBar = new ProgressBar();

            {
                container.setPadding(new Insets(10));
                name.setFont(Font.font("Segoe UI Semibold", 14));
                deadline.setFont(Font.font("Segoe UI", 12));
                progressBar.setPrefWidth(200);
                progressBar.setStyle("-fx-accent: #10b981;");
                container.getChildren().addAll(name, deadline, progressBar);
                setGraphic(container);
            }

            @Override
            protected void updateItem(Project p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) setGraphic(null);
                else {
                    String unassignedText = p.getManagerId() == null ? " (Unassigned)" : "";
                    name.setText(p.getName() + unassignedText);
                    deadline.setText("Deadline: " + p.getDeadline());
                    progressBar.setProgress(p.getProgress() / 100.0);
                    setGraphic(container);
                }
            }
        });
    }

    private void setupTaskListView() {
        tasksList.setCellFactory(lv -> new ListCell<>() {
            private final VBox container = new VBox(6);
            private final Label desc = new Label();
            private final Label assigned = new Label();
            private final CheckBox completed = new CheckBox("Completed");
            private Task currentTask;

            {
                container.setPadding(new Insets(10));
                desc.setFont(Font.font("Segoe UI", 14));
                assigned.setFont(Font.font("Segoe UI", 13));
                container.getChildren().addAll(desc, assigned, completed);
                setGraphic(container);

                completed.setOnAction(e -> {
                    if (currentTask != null) {
                        currentTask.setCompleted(completed.isSelected());
                        Project project = projectsList.getSelectionModel().getSelectedItem();
                        if (project != null) {
                            project.updateProgress();
                            progressField.setText(String.valueOf((int) project.getProgress()));
                        }
                        ProjectRepository.saveAll();
                        projectsList.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);
                currentTask = t;
                if (empty || t == null) setGraphic(null);
                else {
                    desc.setText(t.getDescription());
                    Member m = UserRepository.getMemberById(t.getAssignedMemberId());
                    assigned.setText("Assigned to: " + (m != null ? m.getName() : "Unknown"));
                    completed.setSelected(t.isCompleted());
                    setGraphic(container);
                }
            }
        });

        tasksList.setPlaceholder(tasksPlaceholderLabel);
    }

    private void loadAISuggestions(Project project) {
        List<String> suggestions = RiskAISuggester.suggest(project);
        suggestionsArea.setText(
                (suggestions == null || suggestions.isEmpty())
                        ? "No suggestions available."
                        : String.join("\n", suggestions)
        );
    }

   @FXML
public void onAssignTask(javafx.event.ActionEvent event) {
    Project selected = projectsList.getSelectionModel().getSelectedItem();
    Member member = memberComboBox.getSelectionModel().getSelectedItem();
    String desc = taskDescField.getText().trim();

    if (selected == null || member == null || desc.isEmpty()) {
        statusLabel.setText("Fill all task fields.");
        return;
    }

    // ✅ Assign manager only if not already assigned
    if (selected.getManagerId() == null) {
        selected.setManagerId(loggedInManager.getId());
        // No need to recreate ObservableList
        projectsList.refresh(); // Just refresh the ListView
    }

    // Create new task with unique ID
    Task newTask = new Task(desc, desc, member.getId(), false);

    // Add task safely (Project.addTask prevents duplicates)
    selected.addTask(newTask);

    // Save to repository
    ProjectRepository.saveAll();

    // 🔹 Update tasksList immediately without creating a new list
    if (!tasksList.getItems().contains(newTask)) {
        tasksList.getItems().add(newTask);
    }
    tasksList.refresh();

    taskDescField.clear();
    statusLabel.setText("Task assigned successfully.");
}


    @FXML
    public void onUpdateProgress(javafx.event.ActionEvent event) {
        Project project = projectsList.getSelectionModel().getSelectedItem();
        if (project == null) {
            statusLabel.setText("Select a project first!");
            return;
        }
        project.updateProgress();
        ProjectRepository.saveAll();
        projectsList.refresh();
        progressField.setText(String.valueOf((int) project.getProgress()));
        statusLabel.setText("Progress recalculated based on tasks.");
    }

    @FXML
    public void onBackToLogin(javafx.event.ActionEvent event) {
        SceneController.switchTo("Login.fxml");
    }
}
