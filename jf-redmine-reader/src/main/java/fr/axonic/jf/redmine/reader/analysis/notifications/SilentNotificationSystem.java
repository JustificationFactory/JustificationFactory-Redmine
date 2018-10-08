package fr.axonic.jf.redmine.reader.analysis.notifications;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalIssue;
import fr.axonic.jf.redmine.reader.users.UserIdentity;

import java.util.List;

public class SilentNotificationSystem extends NotificationSystem {

    public SilentNotificationSystem() {
        super(null);
    }

    @Override
    public void notify(NotificationContent content) {
        // Nothing here.
    }

    @Override
    protected void notifyProjectManager(UserIdentity user, List<ApprovalIssue> userIssues, List<ApprovalIssue> otherIssues, List<JustificationDocument> validatedDocuments) {
        // Nothing here.
    }

    @Override
    protected void notifyUser(UserIdentity user, List<ApprovalIssue> userIssues) {
        // Nothing here.
    }
}
