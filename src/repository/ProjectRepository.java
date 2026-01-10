package repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.Project;
import models.Task;
import utils.JSONFileHandler;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

public class ProjectRepository {

    private static final Path PATH = Path.of("data/projects.json");
    private static final List<Project> projects = new ArrayList<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 🔧 FIX: ensure projects are loaded only once
    private static boolean loaded = false;

    /** Load all projects from JSON file */
    public static void loadAll() {
        if (loaded) return; // 🔧 FIX

        try {
            String json = JSONFileHandler.read(PATH.toString());

            if (json == null || json.trim().isEmpty()) {
                JSONFileHandler.write(PATH.toString(), "[]");
                projects.clear();
            } else {
                Type listType = new TypeToken<List<Project>>() {}.getType();
                List<Project> loadedProjects = gson.fromJson(json, listType);

                projects.clear();
                if (loadedProjects != null) {
                    for (Project p : loadedProjects) {
                        if (p.getTasks() == null) p.setTasks(new ArrayList<>());
                        p.updateProgress();
                        projects.add(p);
                    }
                }
            }

            loaded = true; // 🔧 FIX

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Save all projects to JSON file */
    public static void saveAll() {
        try {
            java.nio.file.Files.createDirectories(PATH.getParent());

            // Ensure progress is up-to-date before saving
            for (Project p : projects) {
                p.updateProgress();
            }

            String out = gson.toJson(projects);
            JSONFileHandler.write(PATH.toString(), out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Get all projects */
    public static List<Project> getAll() {
        loadAll();
        return projects; // 🔧 FIX: return shared state, not a copy
    }

    /** Get projects assigned to a specific manager */
    public static List<Project> getByManagerId(String managerId) {
        loadAll();
        List<Project> managerProjects = new ArrayList<>();

        for (Project p : projects) {
            if (managerId != null && managerId.equals(p.getManagerId())) {
                managerProjects.add(p);
            }
        }
        return managerProjects;
    }

    /** Add a new project */
    public static void add(Project project) {
        if (project != null) {
            loadAll();
            project.updateProgress();
            projects.add(project);
            saveAll();
        }
    }

    /** Find project by ID */
    public static Project findById(String id) {
        loadAll();
        for (Project p : projects) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    /** Delete project by ID */
    public static void delete(String id) {
        loadAll();
        projects.removeIf(p -> p.getId().equals(id));
        saveAll();
    }

    /** Remove a project */
    public static boolean remove(Project project) {
        loadAll();
        if (project != null) {
            boolean removed = projects.remove(project);
            if (removed) saveAll();
            return removed;
        }
        return false;
    }

    /** Generate a unique project ID */
    public static String generateId() {
        return "prj-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /** Update project when a task's progress changes */
    public static void updateProjectForTask(Task task) {
        if (task == null) return;

        loadAll();
        for (Project p : projects) {
            for (Task t : p.getTasks()) {
                if (t.getId().equals(task.getId())) {
                    p.updateProgress();
                    saveAll();
                    return;
                }
            }
        }
    }
}
