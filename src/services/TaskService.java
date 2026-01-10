package services;

import models.Member;
import models.Project;
import models.Task;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {

    /**
     * Returns tasks belonging to a project.
     * Works with projectId safely.
     */
    public static List<Task> getTasksForProject(Project p) {
        if (p == null) return new ArrayList<>();
        return p.getTasks();
    }

    /**
     * Returns least busy member:
     * defined as member with the fewest assigned tasks.
     */
    public static Member getLeastBusyMember(Project p) {
        List<Member> members = UserService.getTeamMembers();
        if (members.isEmpty()) return null;

        return members.stream()
                .min((m1, m2) -> {
                    long m1Tasks = p.getTasks().stream().filter(t -> t.getAssignedTo().equals(m1.getId())).count();
                    long m2Tasks = p.getTasks().stream().filter(t -> t.getAssignedTo().equals(m2.getId())).count();
                    return Long.compare(m1Tasks, m2Tasks);
                })
                .orElse(members.get(0));
    }

    /**
     * Generate text report for a single project.
     */
    public static String getReportForProject(Project p) {
        if (p == null) return "No project selected.";

        StringBuilder sb = new StringBuilder();
        sb.append("Project: ").append(p.getName()).append("\n");
        sb.append("------------------------------------------------\n");

        for (Task t : p.getTasks()) {
            Member assigned = UserRepository.getMemberById(t.getAssignedTo());
            String memberName = (assigned != null) ? assigned.getName() : "Unknown";

            sb.append("- ")
              .append(t.getDescription())
              .append(" | Assigned to: ").append(memberName)
              .append(" | Status: ").append(t.isCompleted() ? "Completed" : "Pending")
              .append("\n");
        }

        return sb.toString();
    }
}