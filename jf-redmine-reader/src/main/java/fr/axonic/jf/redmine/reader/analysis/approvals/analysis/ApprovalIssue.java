package fr.axonic.jf.redmine.reader.analysis.approvals.analysis;

import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.users.UserIdentity;

import java.util.Optional;

public class ApprovalIssue {

    private final UserIdentity user;
    private final ApprovalIssueType issueType;
    private final ApprovalDocument approvalDocument;

    public ApprovalIssue(UserIdentity user, ApprovalIssueType issueType, ApprovalDocument approvalDocument) {
        this.user = user;
        this.issueType = issueType;
        this.approvalDocument = approvalDocument;
    }

    public ApprovalIssue(ApprovalIssueType issueType, ApprovalDocument approvalDocument) {
        user = null;
        this.issueType = issueType;
        this.approvalDocument = approvalDocument;
    }

    public Optional<UserIdentity> getUser() {
        return Optional.ofNullable(user);
    }

    public ApprovalIssueType getIssueType() {
        return issueType;
    }

    public ApprovalDocument getApprovalDocument() {
        return approvalDocument;
    }
}
