package fr.axonic.jf.redmine.reader.configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationDocument {

    private RedmineCredentials redmineCredentials;
    private EmailCredentials emailCredentials;
    private RedmineDatabaseCredentials redmineDatabaseCredentials;
    private String justificationFactoryBusUrl;
    private String rankingSambaFolder;
    private List<ProjectConfiguration> projects;

    public ConfigurationDocument() {
        projects = new ArrayList<>();
    }

    public RedmineCredentials getRedmineCredentials() {
        return redmineCredentials;
    }

    public void setRedmineCredentials(RedmineCredentials redmineCredentials) {
        this.redmineCredentials = redmineCredentials;
    }

    public EmailCredentials getEmailCredentials() {
        return emailCredentials;
    }

    public void setEmailCredentials(EmailCredentials emailCredentials) {
        this.emailCredentials = emailCredentials;
    }

    public RedmineDatabaseCredentials getRedmineDatabaseCredentials() {
        return redmineDatabaseCredentials;
    }

    public void setRedmineDatabaseCredentials(RedmineDatabaseCredentials redmineDatabaseCredentials) {
        this.redmineDatabaseCredentials = redmineDatabaseCredentials;
    }

    public String getJustificationFactoryBusUrl() {
        return justificationFactoryBusUrl;
    }

    public void setJustificationFactoryBusUrl(String justificationFactoryBusUrl) {
        this.justificationFactoryBusUrl = justificationFactoryBusUrl;
    }

    public String getRankingSambaFolder() {
        return rankingSambaFolder;
    }

    public void setRankingSambaFolder(String rankingSambaFolder) {
        this.rankingSambaFolder = rankingSambaFolder;
    }

    public List<ProjectConfiguration> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectConfiguration> projects) {
        this.projects = projects;
    }
}
