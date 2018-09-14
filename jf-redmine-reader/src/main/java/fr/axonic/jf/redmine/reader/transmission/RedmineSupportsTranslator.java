package fr.axonic.jf.redmine.reader.transmission;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.engine.support.evidence.Document;
import fr.axonic.avek.instance.redmine.RedmineDocumentApproval;
import fr.axonic.avek.instance.redmine.RedmineDocumentEvidence;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.configuration.RedmineCredentials;
import fr.axonic.jf.redmine.reader.transmission.metadata.AxonicMetadataExtractor;
import fr.axonic.jf.redmine.reader.transmission.metadata.MetadataExtractor;

public class RedmineSupportsTranslator {

    private final RedmineCredentials redmineCredentials;
    private final ProjectStatus currentProject;
    private final MetadataExtractor extractor;

    public RedmineSupportsTranslator(RedmineCredentials redmineCredentials, ProjectStatus currentProject) {
        this.redmineCredentials = redmineCredentials;
        this.currentProject = currentProject;

        extractor = new AxonicMetadataExtractor(redmineCredentials, currentProject); // TODO Put it in the constructor.
    }

    public RedmineDocumentEvidence translateEvidence(ApprovalDocument approval) {
        WikiPage page = approval.getSource().getAssociatedPage();

        return new RedmineDocumentEvidence(pageName(page), extractor.extractMetadata(approval));
    }

    public RedmineDocumentApproval translateApproval(ApprovalDocument approval) {
        WikiPage page = approval.getSource().getAssociatedPage();

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
        return redmineCredentials.getUrl() + "/" + currentProject + "/wiki/" + page.getTitle();
    }
}
