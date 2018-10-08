package fr.axonic.jf.redmine.reader.analysis.approvals.extraction;

import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.users.bindings.ProjectIdentityBinder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

public abstract class ApprovalDocumentExtractor {

    public static final LocalDate WRONG_DATE = LocalDate.of(0, Month.JANUARY, 1);

    private final ProjectIdentityBinder identityBinder;

    public ApprovalDocumentExtractor(ProjectIdentityBinder identityBinder) {
        this.identityBinder = identityBinder;
    }

    protected ProjectIdentityBinder getIdentityBinder() {
        return identityBinder;
    }

    public abstract Optional<ApprovalDocument> extract(WikiPage wikiPage, WikiPageDetail pageDetail);
}
