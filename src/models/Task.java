package models;

import java.util.UUID;

public class Task {

    private String id;

    // 🔴 KEEP name (used elsewhere)
    private String name;

    // ✅ ADD description (USED BY DASHBOARD)
    private String description;

    private String assignedMemberId;
    private boolean completed;
    private double progress; // 0–100 progress value

    // ===== Constructors =====
    public Task() {}

    // Full constructor (existing)
    public Task(String id, String name, String description,
                String assignedMemberId, boolean completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.assignedMemberId = assignedMemberId;
        this.completed = completed;
        this.progress = 0;
    }

    // ✅ NEW: Convenient constructor without ID
    public Task(String name, String description, String assignedMemberId, boolean completed) {
        this.id = generateId(); // auto-generate ID
        this.name = name;
        this.description = description;
        this.assignedMemberId = assignedMemberId;
        this.completed = completed;
        this.progress = 0;
    }

    // ===== Getters & Setters =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() {
        return (description != null && !description.isEmpty()) ? description : name;
    }

    public void setDescription(String description) { this.description = description; }

    public String getAssignedMemberId() { return assignedMemberId; }
    public void setAssignedMemberId(String assignedMemberId) { this.assignedMemberId = assignedMemberId; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public double getProgress() { return progress; }

    // ✅ FIXED: clamp progress 0–100 & auto-update completed flag
    public void setProgress(double progress) {
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;

        this.progress = progress;
        this.completed = (progress >= 100);
    }

    @Override
    public String toString() {
        return getDescription() + " (" + progress + "%)" + (completed ? " ✅" : "");
    }

    public static String generateId() {
        return "tsk-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
