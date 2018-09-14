package fr.axonic.jf.redmine.reader.analysis.notifications;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalIssue;

import java.util.List;

public class NotificationContent {

    private List<ApprovalIssue> listedIssuesInApproval;
    private List<JustificationDocument> validatedJustificationDocuments;

    public List<ApprovalIssue> getListedIssuesInApproval() {
        return listedIssuesInApproval;
    }

    public void setListedIssuesInApproval(List<ApprovalIssue> listedIssuesInApproval) {
        this.listedIssuesInApproval = listedIssuesInApproval;
    }

    public List<JustificationDocument> getValidatedJustificationDocuments() {
        return validatedJustificationDocuments;
    }

    public void setValidatedJustificationDocuments(List<JustificationDocument> validatedJustificationDocuments) {
        this.validatedJustificationDocuments = validatedJustificationDocuments;
    }
}
