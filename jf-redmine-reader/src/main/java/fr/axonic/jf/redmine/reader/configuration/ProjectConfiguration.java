package fr.axonic.jf.redmine.reader.configuration;

import java.util.ArrayList;
import java.util.List;

public class ProjectConfiguration {

    private String projectName;
    private List<String> ignoredDocuments;

    public ProjectConfiguration() {
        ignoredDocuments = new ArrayList<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getIgnoredDocuments() {
        return ignoredDocuments;
    }

    public void setIgnoredDocuments(List<String> ignoredDocuments) {
        this.ignoredDocuments = ignoredDocuments;
    }
}
