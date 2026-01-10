package models;

public class MemberTask {
    private String description;
    private int progress; // 0-100

    public MemberTask() {}

    public MemberTask(String description, int progress) {
        this.description = description;
        this.progress = progress;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}