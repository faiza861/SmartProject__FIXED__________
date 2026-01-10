package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {

    private String id;
    private String name;
    private String description;
    private String deadline; // ISO date string
    private double progress; // 0-100
    private String clientId;
    private String managerId;

    // Observable list for JavaFX binding
    private transient ObservableList<Task> observableTasks = FXCollections.observableArrayList();

    // Plain list for serialization
    private List<Task> tasks = new ArrayList<>();

    public Project() {}

    public Project(String id, String name, String description, String deadline,
                   String clientId, String managerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.clientId = clientId;
        this.managerId = managerId;
        this.progress = 0;
    }

    public static String generateId() {
        return "prj-" + UUID.randomUUID().toString().substring(0, 6);
    }

    // ----- Getters & Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    // ----- Task Management -----
    public ObservableList<Task> getTasks() {
        // Sync observableTasks with plain list
        if (observableTasks.isEmpty() && !tasks.isEmpty()) {
            observableTasks.addAll(tasks);
        }
        return observableTasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        observableTasks.setAll(this.tasks);
        updateProgress();
    }

    public void addTask(Task task) {
        boolean exists = tasks.stream().anyMatch(t -> t.getId().equals(task.getId()));
        if (!exists) {
            tasks.add(task);
            observableTasks.add(task);
            updateProgress();
        }
    }

    public void updateProgress() {
        if (tasks.isEmpty()) {
            this.progress = 0;
            return;
        }
        double total = tasks.stream().mapToDouble(Task::getProgress).sum();
        this.progress = total / tasks.size();
    }

    @Override
    public String toString() {
        return name + " (" + (int)progress + "%) - Deadline: " + deadline;
    }
}
