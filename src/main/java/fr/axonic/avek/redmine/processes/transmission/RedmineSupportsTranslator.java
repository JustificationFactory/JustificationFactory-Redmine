package fr.axonic.avek.redmine.processes.transmission;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.engine.support.evidence.Document;
import fr.axonic.avek.instance.redmine.RedmineDocumentApproval;
import fr.axonic.avek.instance.redmine.RedmineDocumentEvidence;
import fr.axonic.avek.redmine.io.models.ConfigurationDocument;

public class RedmineSupportsTranslator {

    private final ConfigurationDocument runnerConfiguration;
    private String currentProject;

    public RedmineSupportsTranslator(ConfigurationDocument runnerConfiguration) {
        this.runnerConfiguration = runnerConfiguration;
    }

    public void setCurrentProject(String currentProject) {
        this.currentProject = currentProject;
    }

    public RedmineDocumentEvidence translateEvidence(WikiPage page) {
        Document document = new Document(getPageUrl(page));
        document.setVersion(version(page));

        return new RedmineDocumentEvidence(pageName(page), document);
    }

    public RedmineDocumentApproval translateApproval(WikiPage page) {
        Document document = new Document(getPageUrl(page) + "#APPROBATION-DU-DOCUMENT");
        document.setVersion(version(page));

        return new RedmineDocumentApproval(
                pageName(page) + "_APPROVAL",
                document);
    }

    private String pageName(WikiPage page) {
        String fullTitle = page.getTitle();

        if (fullTitle.matches(".*_[A-Z]")) {
            return fullTitle.substring(0, fullTitle.length() - 2);
        }

        return page.getTitle();
    }

    private String version(WikiPage page) {
        String fullTitle = page.getTitle();

        if (fullTitle.matches(".*_[A-Z]")) {
            return fullTitle.substring(fullTitle.length() - 1);
        }

        return "A";
    }

    private String getPageUrl(WikiPage page) {
        return runnerConfiguration.getRedmineUrl() + "/" + currentProject + "/wiki/" + page.getTitle();
    }
}
