package fr.axonic.avek.redmine.analysis.approvals.extraction;

import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.avek.redmine.analysis.approvals.ApprovalDocument;
import fr.axonic.avek.redmine.users.bindings.IdentityBinder;

import java.util.Optional;

public abstract class ApprovalExtractor {

    private final IdentityBinder identityBinder;

    public ApprovalExtractor(IdentityBinder identityBinder) {
        this.identityBinder = identityBinder;
    }

    protected IdentityBinder getIdentityBinder() {
        return identityBinder;
    }

    public abstract Optional<ApprovalDocument> extract(WikiPage wikiPage, WikiPageDetail pageDetail);
}
