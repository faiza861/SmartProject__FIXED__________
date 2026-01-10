package services;

import models.Project;
import java.util.ArrayList;
import java.util.List;

public class RiskAISuggester {

    public static List<String> suggest(Project project) {
        List<String> suggestions = new ArrayList<>();
        if (project == null) return suggestions;

        double progress = project.getProgress();

          // ----- Progress analysis -----
        if (progress < 20) {
            suggestions.add(
        "The current project progress is critically low compared to the expected timeline. "
      + "This indicates possible planning issues, unresolved blockers, or uneven task distribution. "
      + "A detailed review of project scope, task priorities, and team responsibilities is strongly recommended to prevent further delays."
    );
        } else if (progress < 50) {
            suggestions.add(
        "The project has not yet reached fifty percent completion, which suggests that progress is slower than planned. "
      + "Management should review current resource allocation, evaluate task complexity, and consider timeline adjustments to improve overall execution."
    );
        } else if (progress < 80) {
            suggestions.add(
        "The project is progressing at a stable and acceptable pace at this stage. "
      + "However, continuous monitoring of milestones, deadlines, and risk factors is important to ensure that progress remains consistent and controlled."
    );
        } else {
            suggestions.add(
        "The project is approaching its final stages of completion. "
      + "Attention should now be directed toward final testing, documentation, validation, and quality assurance to ensure a smooth and successful delivery."
    );
}

        // ----- Task analysis -----
        if (project.getTasks() == null || project.getTasks().isEmpty()) {
            suggestions.add(
        "No tasks have been defined for this project, which may indicate an incomplete or unclear project plan. "
      + "Breaking the project into well-defined tasks will improve tracking, accountability, and execution efficiency."
    );
            return suggestions;
}

        long incomplete = project.getTasks().stream().filter(t->!t.isCompleted()).count();

        if (incomplete > 5) {
            suggestions.add(
        "A high number of tasks are still incomplete at this stage of the project. "
      + "This significantly increases the risk of schedule slippage and resource overload, and may require immediate intervention from project management."
    );
        } else if (incomplete > 0) {
            suggestions.add(
        "Some project tasks remain unfinished, which is expected during ongoing execution. "
      + "Ensuring balanced workload distribution and conducting regular progress reviews can help prevent these tasks from becoming bottlenecks."
    );
        } else {
            suggestions.add(
        "All defined project tasks have been completed successfully. "
      + "This indicates strong execution and effective planning, suggesting that the project is well aligned with its goals and delivery timeline."
    );
}

        return suggestions;

    }

    /** Compute risk score for a project (0.0 to 1.0) */
    public static double computeRisk(Project project) {
        if (project == null) return 1.0;

        double risk = 0.0;

        // Factor 1: Low progress increases risk
        if (project.getProgress() < 30) risk += 0.4;
        else if (project.getProgress() < 60) risk += 0.2;
        else if (project.getProgress() < 90) risk += 0.1;

        // Factor 2: Many incomplete tasks increase risk
        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
            long incomplete = project.getTasks().stream().filter(t -> !t.isCompleted()).count();
            double ratio = (double) incomplete / project.getTasks().size();
            risk += ratio * 0.3;
        } else {
            risk += 0.3; // No tasks defined = high risk
        }

        // Cap risk at 1.0
        return Math.min(risk, 1.0);
    }
}