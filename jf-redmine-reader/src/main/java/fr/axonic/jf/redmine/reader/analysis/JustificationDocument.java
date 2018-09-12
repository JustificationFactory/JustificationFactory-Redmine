package fr.axonic.jf.redmine.reader.analysis;

import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;

public class JustificationDocument {

    private final WikiPage associatedPage;
    private final WikiPageDetail pageDetail;
    private final ApprovalDocument approval;

    public JustificationDocument(WikiPage associatedPage, WikiPageDetail pageDetail, ApprovalDocument approval) {
        this.associatedPage = associatedPage;
        this.pageDetail = pageDetail;
        this.approval = approval;
    }

    public WikiPage getAssociatedPage() {
        return associatedPage;
    }

    public WikiPageDetail getPageDetail() {
        return pageDetail;
    }

    public ApprovalDocument getApproval() {
        return approval;
    }
}
