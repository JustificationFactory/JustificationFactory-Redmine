package fr.axonic.jf.redmine.reader.analysis.approvals.analysis;

import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalSignature;
import fr.axonic.jf.redmine.reader.analysis.approvals.extraction.ApprovalDocumentExtractor;
import fr.axonic.jf.redmine.reader.users.UserRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApprovalDocumentAnalyzer {

    public List<ApprovalIssue> analyze(ApprovalDocument approval) {
        ApprovalDocumentAnalysisRunner runner = new ApprovalDocumentAnalysisRunner(approval);
        runner.runAnalysis();

        return runner.getIssues();
    }

    private static class ApprovalDocumentAnalysisRunner {

        private final List<ApprovalIssue> issues;
        private final ApprovalDocument approval;

        ApprovalDocumentAnalysisRunner(ApprovalDocument approval) {
            this.approval = approval;

            issues = new ArrayList<>();
        }

        List<ApprovalIssue> getIssues() {
            return issues;
        }

        void runAnalysis() {
            if (approval.getSignatures().stream().noneMatch(signature -> signature.getSignatoryRole() == UserRole.AUTHOR)) {
                issue(ApprovalIssueType.NO_AUTHOR);
            }

            if (approval.getSignatures().stream().noneMatch(signature -> signature.getSignatoryRole() == UserRole.VERIFIER)) {
                issue(ApprovalIssueType.NO_VERIFIER);
            }

            approval.getSignatures().stream().filter(s -> s.getSignatoryRole() == UserRole.AUTHOR).forEach(this::analyzeAuthor);

            if (issues.stream().noneMatch(i -> i.getIssueType() == ApprovalIssueType.NOT_SIGNED_AS_AUTHOR)) {
                approval.getSignatures().stream().filter(s -> s.getSignatoryRole() == UserRole.VERIFIER).forEach(this::analyzeVerifier);
            }
        }

        private void analyzeAuthor(ApprovalSignature signature) {
            if (signature.isConfirmed()) {
                analyzeUser(signature);
            } else {
                issue(ApprovalIssueType.NOT_SIGNED_AS_AUTHOR, signature);
            }
        }

        private void analyzeVerifier(ApprovalSignature signature) {
            if (signature.isConfirmed()) {
                analyzeUser(signature);
            } else {
                issue(ApprovalIssueType.NOT_SIGNED_AS_VERIFIER, signature);
            }
        }

        private void analyzeUser(ApprovalSignature signature) {
            Optional<LocalDate> signedDate = signature.getSignedDate();
            if (signedDate.isPresent()) {
                if (signedDate.get() == ApprovalDocumentExtractor.WRONG_DATE) {
                    issue(ApprovalIssueType.INVALID_DATE, signature);
                }
            } else {
                issue(ApprovalIssueType.MISSING_DATE, signature);
            }
        }

        private void issue(ApprovalIssueType issueType) {
            issues.add(new ApprovalIssue(issueType, approval));
        }

        private void issue(ApprovalIssueType issueType, ApprovalSignature signature) {
            issues.add(new ApprovalIssue(signature.getSignatory(), issueType, approval));
        }
    }
}
