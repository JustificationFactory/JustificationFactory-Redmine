package fr.axonic.jf.redmine.reader.transmission.metadata;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.jf.instance.redmine.RedmineDocument;
import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.configuration.RedmineCredentials;
import fr.axonic.jf.redmine.reader.users.UserRole;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AxonicMetadataExtractor implements MetadataExtractor {

    private static final Pattern REDMINE_FILE_NAME = Pattern.compile("([A-Za-z0-9_]+)_([A-Z]+)");
    private final RedmineCredentials redmineCredentials;
    private final ProjectStatus currentProject;

    public AxonicMetadataExtractor(RedmineCredentials redmineCredentials, ProjectStatus currentProject) {
        this.redmineCredentials = redmineCredentials;
        this.currentProject = currentProject;
    }

    @Override
    public RedmineDocument extractMetadata(WikiPage wikiPage) {
        RedmineDocument document = new RedmineDocument(pageUrl(wikiPage));
        document.setVersion(version(wikiPage));
        document.setReference(reference(wikiPage));
        document.setDocumentType(documentType(wikiPage));
        document.setReleaseDate(releaseDate(wikiPage));
        document.setName(name(wikiPage));

        return document;
    }

    @Override
    public RedmineDocument extractMetadata(ApprovalDocument approval) {
        WikiPage wikiPage = approval.getSource().getAssociatedPage();

        RedmineDocument document = new RedmineDocument(pageUrl(wikiPage));
        document.setVersion(version(wikiPage));
        document.setAuthor(author(approval));
        document.setName(name(approval));
        document.setReference(reference(wikiPage));
        document.setDocumentType(documentType(wikiPage));
        document.setReleaseDate(releaseDate(wikiPage));

        return document;
    }

    private String name(WikiPage wikiPage) {
        Matcher m = REDMINE_FILE_NAME.matcher(wikiPage.getTitle());
        if (m.matches()) {
            return m.group(1);
        } else {
            return "UNKNOWN";
        }
    }

    private LocalDate releaseDate(WikiPage page) {
        return page.getUpdatedOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String author(ApprovalDocument approval) {
        return approval.getSignatures().stream()
                .filter(s -> s.getSignatoryRole() == UserRole.AUTHOR)
                .findFirst()
                .map(s -> s.getSignatory().getInitials())
                .orElse("UNKNOWN_AUTHOR");
    }

    private String name(ApprovalDocument approval) {
        JustificationDocument document = approval.getSource();

        if (document != null) {
            return name(document.getAssociatedPage());
        } else {
            return "UNKNOWN_NAME";
        }
    }

    private String documentType(WikiPage page) {
        String[] parts = page.getTitle().split("_");

        if (parts[0].startsWith("SWAM")) {
            return parts[1];
        } else {
            return "UNKNOWN_DOCUMENT_TYPE";
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
        return redmineCredentials.getUrl() + "/" + currentProject + "/wiki/" + page.getTitle();
    }
}
