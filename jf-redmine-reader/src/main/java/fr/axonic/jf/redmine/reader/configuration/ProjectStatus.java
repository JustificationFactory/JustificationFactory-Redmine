package fr.axonic.jf.redmine.reader.configuration;

import java.time.LocalDateTime;

public class ProjectStatus {

    private String projectName;
    private LocalDateTime lastAnalysisTime;

    public ProjectStatus(String projectName) {
        this.projectName = projectName;
        this.lastAnalysisTime = LocalDateTime.MIN;
    }

    public ProjectStatus() {
        lastAnalysisTime = LocalDateTime.MIN;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDateTime getLastAnalysisTime() {
        return lastAnalysisTime;
    }

    public void setLastAnalysisTime(LocalDateTime lastAnalysisTime) {
        this.lastAnalysisTime = lastAnalysisTime;
    }
}
