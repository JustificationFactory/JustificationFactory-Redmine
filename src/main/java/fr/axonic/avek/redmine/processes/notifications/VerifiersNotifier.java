package fr.axonic.avek.redmine.processes.notifications;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.redmine.models.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class VerifiersNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifiersNotifier.class);

    private Map<UserIdentity, List<VerifiersNotification>> registeredNotifications;

    public VerifiersNotifier() {
        registeredNotifications = new HashMap<>();
    }

    public void missingDate(UserIdentity user, WikiPage wikiPage) {
        makeNotification(NotificationTopic.MISSING_DATE, user, wikiPage);
    }

    public void signedBeforeAuthorValidation(UserIdentity user, WikiPage wikiPage) {
        makeNotification(NotificationTopic.SIGNED_BEFORE_AUTHOR_VALIDATION, user, wikiPage);
    }

    public void notSigned(UserIdentity user, WikiPage wikiPage) {
        makeNotification(NotificationTopic.NOT_SIGNED, user, wikiPage);
    }

    public void noAuthor(WikiPage wikiPage) {
        makeNotification(NotificationTopic.NO_AUTHOR, new UserIdentity("QUALITY"), wikiPage);
    }

    private void makeNotification(NotificationTopic topic, UserIdentity user, WikiPage wikiPage) {
        if (user == null || user.getInitials() == null) {
            return;
        }

        if (!registeredNotifications.containsKey(user)) {
            registeredNotifications.put(user, new ArrayList<>());
        }

        registeredNotifications.get(user).add(new VerifiersNotification(topic, user, wikiPage));
    }

    public void processNotifications() {
        registeredNotifications.forEach((user, notifications) -> {
            try {
                processUserNotifications(user, notifications);
            } catch (IOException e) {
                LOGGER.error("Failed to send the notifications to {}.", user.getInitials(), e);
            }
        });
    }

    protected abstract void processUserNotifications(UserIdentity identity, List<VerifiersNotification> notifications) throws IOException;
}
