package fr.axonic.avek.redmine.analysis.approvals;

import com.taskadapter.redmineapi.bean.WikiPage;

import java.util.ArrayList;
import java.util.List;

public class ApprovalDocument {

    public static final ApprovalDocument INVALID_DOCUMENT = new ApprovalDocument(null);

    private final WikiPage wikiPage;
    private final List<ApprovalSignature> signatures;

    public ApprovalDocument(WikiPage wikiPage) {
        this.wikiPage = wikiPage;
        signatures = new ArrayList<>();
    }

    public List<ApprovalSignature> getSignatures() {
        return signatures;
    }

    public WikiPage getWikiPage() {
        return wikiPage;
    }
}
