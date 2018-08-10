package fr.axonic.avek.redmine.analysis.approvals.verification;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.redmine.analysis.reporting.AnalysisReport;
import fr.axonic.avek.redmine.analysis.approvals.ApprovalDocument;
import fr.axonic.avek.redmine.analysis.approvals.ApprovalSignature;
import fr.axonic.avek.redmine.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.NotificationType;
import fr.axonic.avek.redmine.analysis.notifications.UserNotification;
import fr.axonic.avek.redmine.users.UserIdentity;
import fr.axonic.avek.redmine.users.UserRole;
import fr.axonic.avek.redmine.users.bindings.IdentityBinder;

import java.util.List;
import java.util.stream.Collectors;

public class ApprovalVerifier {

    private final NotificationSystem notifier;
    private final IdentityBinder identityBinder;
    private final AnalysisReport report;

    public ApprovalVerifier(NotificationSystem notifier, IdentityBinder identityBinder, AnalysisReport report) {
        this.notifier = notifier;
        this.identityBinder = identityBinder;
        this.report = report;
    }

    public boolean verify(ApprovalDocument approval) {
        if (approval == ApprovalDocument.INVALID_DOCUMENT) {
            // The document does not comply to the formalism.
            return false;
        }

        if (!hasAuthors(approval) || !authorsConfirmed(approval)) {
            // The authors have not locked the document.
            return false;
        }

        return approval.getSignatures().stream()
                .map(s -> verifySignature(approval, s))
                .reduce(Boolean::logicalAnd).orElse(true);
    }

    private boolean hasAuthors(ApprovalDocument document) {
        List<ApprovalSignature> authorsSignatures = document.getSignatures().stream()
                .filter(s -> s.getSignatoryRole() == UserRole.AUTHOR)
                .collect(Collectors.toList());

        if (authorsSignatures.isEmpty() || authorsSignatures.stream().anyMatch(s -> s.getSignatory() == null)) {
            noAuthor(document.getWikiPage());
            return false;
        }

        return true;
    }

    private boolean authorsConfirmed(ApprovalDocument document) {
        boolean ok = true;

        for (ApprovalSignature signature : document.getSignatures()) {
            if (signature.getSignatoryRole() == UserRole.AUTHOR && !signature.isConfirmed()) {
                notSignedAsAuthor(document.getWikiPage(), signature.getSignatory());
                ok = false;
            }
        }

        return ok;
    }

    private boolean verifySignature(ApprovalDocument document, ApprovalSignature signature) {
        if (!signature.isConfirmed()) {
            notSignedAsVerifier(document.getWikiPage(), signature.getSignatory());

            return false;
        }

        boolean ok = true;

        if (!signature.getSignedDate().isPresent()) {
            missingDate(document.getWikiPage(), signature.getSignatory());
            ok = false;
        }

        if (ok) {
            ok(document.getWikiPage(), signature.getSignatory());
        }

        return ok;
    }

    private void ok(WikiPage page, UserIdentity user) {
        notify(new UserNotification(user, page, NotificationType.OK));
    }

    private void noAuthor(WikiPage page) {
        notifier.register(new UserNotification(identityBinder.getDefaultUser(), page, NotificationType.NO_AUTHOR));
    }

    private void notSignedAsAuthor(WikiPage page, UserIdentity user) {
        notify(new UserNotification(user, page, NotificationType.NOT_SIGNED_AS_AUTHOR));
    }

    private void notSignedAsVerifier(WikiPage page, UserIdentity user) {
        notify(new UserNotification(user, page, NotificationType.NOT_SIGNED_AS_VERIFIER));
    }

    private void missingDate(WikiPage page, UserIdentity user) {
        notify(new UserNotification(user, page, NotificationType.MISSING_DATE));
    }

    private void notify(UserNotification notification) {
        report.acknowledge(notification);
        notifier.register(notification);
    }
}
