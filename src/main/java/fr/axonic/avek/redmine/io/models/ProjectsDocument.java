package fr.axonic.avek.redmine.io.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectsDocument {

    private List<ProjectStatus> projects;

    public ProjectsDocument() {
        projects = new ArrayList<>();
    }

    public List<ProjectStatus> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectStatus> projects) {
        this.projects = projects;
    }

    public static class ProjectStatus {

        private String projectName;
        private LocalDateTime lastExecutionTime;

        public ProjectStatus(String projectName, LocalDateTime lastExecutionTime) {
            this.projectName = projectName;
            this.lastExecutionTime = lastExecutionTime;
        }

        public ProjectStatus() {
            // Nothing here.
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public LocalDateTime getLastExecutionTime() {
            return lastExecutionTime;
        }

        public void setLastExecutionTime(LocalDateTime lastExecutionTime) {
            this.lastExecutionTime = lastExecutionTime;
        }
    }
}
