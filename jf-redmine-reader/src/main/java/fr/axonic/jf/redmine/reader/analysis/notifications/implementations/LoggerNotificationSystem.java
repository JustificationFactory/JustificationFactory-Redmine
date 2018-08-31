package fr.axonic.jf.redmine.reader.analysis.notifications.implementations;

import fr.axonic.jf.redmine.reader.analysis.notifications.NotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.UserNotification;
import fr.axonic.jf.redmine.reader.users.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggerNotificationSystem extends NotificationSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerNotificationSystem.class);

    @Override
    protected void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) {
        LOGGER.info("For {}:", identity.getInitials());

        accumulatedNotifications.forEach(n -> LOGGER.info("{} :: {} :: {}", identity.getInitials(), n.getPage().getTitle(), n.getType()));
    }
}
