package fr.axonic.avek.redmine.reader.analysis.approvals.extraction;

import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.avek.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.avek.redmine.reader.users.bindings.IdentityBinder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

public abstract class ApprovalExtractor {

    public static final LocalDate WRONG_DATE = LocalDate.of(0, Month.JANUARY, 1);

    private final IdentityBinder identityBinder;

    public ApprovalExtractor(IdentityBinder identityBinder) {
        this.identityBinder = identityBinder;
    }

    protected IdentityBinder getIdentityBinder() {
        return identityBinder;
    }

    public abstract Optional<ApprovalDocument> extract(WikiPage wikiPage, WikiPageDetail pageDetail);
}
