package fr.axonic.jf.redmine.masterfile;

import fr.axonic.jf.instance.redmine.RedmineDocument;

import java.util.ArrayList;
import java.util.List;

public class MasterFile {

    private final String projectName;
    private final List<RedmineDocument> initializationDocuments;
    private final List<RedmineDocument> entryDataDocuments;
    private final List<RedmineDocument> feasibilityDocuments;
    private final List<RedmineDocument> developmentDocuments;

    public MasterFile(String projectName) {
        this.projectName = projectName;

        initializationDocuments = new ArrayList<>();
        entryDataDocuments = new ArrayList<>();
        feasibilityDocuments = new ArrayList<>();
        developmentDocuments = new ArrayList<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public List<RedmineDocument> getInitializationDocuments() {
        return initializationDocuments;
    }

    public List<RedmineDocument> getEntryDataDocuments() {
        return entryDataDocuments;
    }

    public List<RedmineDocument> getFeasibilityDocuments() {
        return feasibilityDocuments;
    }

    public List<RedmineDocument> getDevelopmentDocuments() {
        return developmentDocuments;
    }
}
