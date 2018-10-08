package fr.axonic.jf.redmine.reader.analysis.approvals.analysis;

public enum ApprovalIssueType {

    MISSING_DATE(ApprovalIssueLevel.ERROR),
    INVALID_DATE(ApprovalIssueLevel.ERROR),
    NOT_SIGNED_AS_AUTHOR(ApprovalIssueLevel.WARNING),
    NOT_SIGNED_AS_VERIFIER(ApprovalIssueLevel.WARNING),
    NO_AUTHOR(ApprovalIssueLevel.ERROR),
    NO_VERIFIER(ApprovalIssueLevel.ERROR);

    private final ApprovalIssueLevel level;

    ApprovalIssueType(ApprovalIssueLevel level) {
        this.level = level;
    }

    public ApprovalIssueLevel getLevel() {
        return level;
    }
}
