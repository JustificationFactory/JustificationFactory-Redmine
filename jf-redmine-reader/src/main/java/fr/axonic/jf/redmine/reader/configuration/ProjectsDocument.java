package fr.axonic.jf.redmine.reader.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<ProjectStatus> getProject(String name) {
        for (ProjectStatus status : projects) {
            if (status.getProjectName().equals(name)) {
                return Optional.of(status);
            }
        }

        return Optional.empty();
    }
}
