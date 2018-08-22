package fr.axonic.avek.redmine.reader.analysis.approvals;

import com.taskadapter.redmineapi.bean.WikiPage;

import java.util.ArrayList;
import java.util.List;

public class ApprovalDocument {

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
