package services;

import models.Project;
import models.Task;

import java.util.ArrayList;
import java.util.List;

public class ProjectService {

    private static final List<Project> projects = new ArrayList<>();

    public static List<Project> getAll() {
        if (projects.isEmpty()) seed();
        return projects;
    }

    public static void saveAll() {
        // Placeholder for JSON saving
    }

    /** Dummy seed data for initial UI load */
    private static void seed() {
        projects.clear();

        Project p1 = new Project("prj-1", "Smart PM", "Project Management System", "2025-12-31", "cli-1", "mgr-1");
        Project p2 = new Project("prj-2", "Website", "Client website redesign", "2025-11-30", "cli-1", "mgr-1");

        p1.addTask(new Task(Task.generateId(), "Setup database", "mem-1", false));
        p1.addTask(new Task(Task.generateId(), "Create login UI", "mem-2", false));

        projects.add(p1);
        projects.add(p2);
    }
}