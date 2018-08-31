package fr.axonic.jf.redmine.reader.transmission.metadata;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.instance.redmine.RedmineDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.configuration.ConfigurationDocument;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.users.UserRole;

import java.time.LocalDate;

public class AxonicMetadataExtractor implements MetadataExtractor {

    private final ConfigurationDocument runnerConfiguration;
    private final ProjectStatus currentProject;

    public AxonicMetadataExtractor(ConfigurationDocument runnerConfiguration, ProjectStatus currentProject) {
        this.runnerConfiguration = runnerConfiguration;
        this.currentProject = currentProject;
    }

    @Override
    public RedmineDocument extractMetadata(WikiPage wikiPage) {
        RedmineDocument document = new RedmineDocument(pageUrl(wikiPage));
        document.setVersion(version(wikiPage));
        document.setReference(reference(wikiPage));
        document.setDocumentType(documentType(wikiPage));
        document.setReleaseDate(releaseDate(wikiPage));

        return document;
    }

    @Override
    public RedmineDocument extractMetadata(ApprovalDocument approval) {
        WikiPage wikiPage = approval.getWikiPage();

        RedmineDocument document = new RedmineDocument(pageUrl(wikiPage));
        document.setVersion(version(wikiPage));
        document.setAuthor(author(approval));
        document.setName(name(approval));
        document.setReference(reference(wikiPage));
        document.setDocumentType(documentType(wikiPage));
        document.setReleaseDate(releaseDate(wikiPage));

        return document;
    }

    private LocalDate releaseDate(WikiPage page) {
        return LocalDate.from(page.getUpdatedOn().toInstant());
    }

    private String author(ApprovalDocument approval) {
        return approval.getSignatures().stream()
                .filter(s -> s.getSignatoryRole() == UserRole.AUTHOR)
                .findFirst()
                .map(s -> s.getSignatory().getInitials())
                .orElse("UNKNOWN_AUTHOR");
    }

    private String name(ApprovalDocument approval) {
        return "UNKNOWN";
    }

    private String documentType(WikiPage page) {
        String[] parts = page.getTitle().split("_");

        if (parts[0].startsWith("SWAM")) {
            return parts[1];
        } else {
            return "UNKNOWN";
        }
    }

    private String reference(WikiPage page) {
        return page.getTitle();
    }

    private String version(WikiPage page) {
        String fullTitle = page.getTitle();

        if (fullTitle.matches(".*_[A-Z]")) {
            return fullTitle.substring(fullTitle.length() - 1);
        }

        return "A";
    }

    private String pageUrl(WikiPage page) {
        return runnerConfiguration.getRedmineUrl() + "/" + currentProject + "/wiki/" + page.getTitle();
    }
}
