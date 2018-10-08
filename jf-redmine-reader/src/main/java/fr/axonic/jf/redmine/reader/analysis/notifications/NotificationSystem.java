package fr.axonic.jf.redmine.reader.analysis.notifications;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalIssue;
import fr.axonic.jf.redmine.reader.users.UserIdentity;
import fr.axonic.jf.redmine.reader.users.bindings.ProjectIdentityBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public abstract class NotificationSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSystem.class);

    private final ProjectIdentityBinder identityBinder;

    public NotificationSystem(ProjectIdentityBinder identityBinder) {
        this.identityBinder = identityBinder;
    }

    public void notify(NotificationContent content) {
        List<ApprovalIssue> projectManagerOtherIssues = new ArrayList<>();

        Map<UserIdentity, List<ApprovalIssue>> sortedIssues = new HashMap<>();

        content.getListedIssuesInApproval().forEach(issue -> {
            Optional<UserIdentity> involvedUser = issue.getUser();

            if (involvedUser.isPresent()) {
                UserIdentity containedUser = involvedUser.get();

                if (identityBinder.knows(containedUser.getInitials())) {
                    if (!sortedIssues.containsKey(containedUser)) {
                        sortedIssues.put(containedUser, new ArrayList<>());
                    }

                    sortedIssues.get(containedUser).add(issue);
                } else {
                    projectManagerOtherIssues.add(issue);
                }
            } else {
                projectManagerOtherIssues.add(issue);
            }
        });

        UserIdentity projectManager = identityBinder.getProjectManager();
        List<ApprovalIssue> projectManagerIssues = sortedIssues.getOrDefault(projectManager, new ArrayList<>());
        sortedIssues.remove(projectManager);

        try {
            notifyProjectManager(projectManager, projectManagerIssues, projectManagerOtherIssues, content.getValidatedJustificationDocuments());
        } catch (IOException e) {
            LOGGER.error("Failed to notify project manager {}.", projectManager.getInitials(), e);
        }

        sortedIssues.forEach((user, issues) -> {
            try {
                notifyUser(user, issues);
            } catch (IOException e) {
                LOGGER.error("Failed to notify user {}.", user.getInitials(), e);
            }
        });
    }

    protected abstract void notifyProjectManager(UserIdentity user, List<ApprovalIssue> userIssues, List<ApprovalIssue> otherIssues, List<JustificationDocument> validatedDocuments) throws IOException;

    protected abstract void notifyUser(UserIdentity user, List<ApprovalIssue> userIssues) throws IOException;
}
