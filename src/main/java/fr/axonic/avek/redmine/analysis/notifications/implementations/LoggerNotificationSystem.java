package fr.axonic.avek.redmine.analysis.notifications.implementations;

import fr.axonic.avek.redmine.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.UserNotification;
import fr.axonic.avek.redmine.users.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class LoggerNotificationSystem extends NotificationSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerNotificationSystem.class);

    public LoggerNotificationSystem(LocalDateTime minimumNotifiableDate) {
        super(minimumNotifiableDate);
    }

    @Override
    protected void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) {
        LOGGER.info("For {}:", identity.getInitials());

        accumulatedNotifications.forEach(n -> LOGGER.info("{} :: {} :: {}", identity.getInitials(), n.getPage().getTitle(), n.getType()));
    }
}
