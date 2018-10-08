package fr.axonic.jf.redmine.reader.analysis.notifications;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalIssue;
import fr.axonic.jf.redmine.reader.users.UserIdentity;
import fr.axonic.jf.redmine.reader.users.bindings.ProjectIdentityBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class LoggerNotificationSystem extends NotificationSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerNotificationSystem.class);

    public LoggerNotificationSystem(ProjectIdentityBinder identityBinder) {
        super(identityBinder);
    }

    @Override
    protected void notifyProjectManager(UserIdentity user, List<ApprovalIssue> userIssues, List<ApprovalIssue> otherIssues, List<JustificationDocument> validatedDocuments) {
        LOGGER.info("For Project Manager {}:", user.getInitials());

        LOGGER.info("Project Manager issues: ");
        userIssues.forEach(n -> LOGGER.info("{} :: {} :: {}", user.getInitials(), n.getApprovalDocument().getSource().getAssociatedPage().getTitle(), n.getIssueType()));

        LOGGER.info("Other issues: ");
        otherIssues.forEach(n -> LOGGER.info("{} :: {} :: {}", n.getUser().map(UserIdentity::getInitials).orElse(""), n.getApprovalDocument().getSource().getAssociatedPage().getTitle(), n.getIssueType()));

        LOGGER.info("Validated documents: {}", validatedDocuments.stream().map(d -> d.getAssociatedPage().getTitle()).collect(Collectors.toList()));
    }

    @Override
    protected void notifyUser(UserIdentity user, List<ApprovalIssue> userIssues) {
        LOGGER.info("For {}:", user.getInitials());
        userIssues.forEach(n -> LOGGER.info("{} :: {} :: {}", user.getInitials(), n.getApprovalDocument().getSource().getAssociatedPage().getTitle(), n.getIssueType()));
    }
}
