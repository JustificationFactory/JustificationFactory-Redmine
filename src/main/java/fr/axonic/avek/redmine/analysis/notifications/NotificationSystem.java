package fr.axonic.avek.redmine.analysis.notifications;

import fr.axonic.avek.redmine.users.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class NotificationSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSystem.class);

    private Map<UserIdentity, List<UserNotification>> registeredNotifications;

    public NotificationSystem() {
        registeredNotifications = new HashMap<>();
    }

    public void register(UserNotification notification) {
        if (notification.getUser() == null || notification.getUser().getInitials() == null) {
            return;
        }

        if (!registeredNotifications.containsKey(notification.getUser())) {
            registeredNotifications.put(notification.getUser(), new ArrayList<>());
        }

        registeredNotifications.get(notification.getUser()).add(notification);
    }

    public void notifyUsers() {
        registeredNotifications.forEach((user, notifications) -> {
            List<UserNotification> usefulNotifications = notifications.stream()
                    .filter(notification -> notification.getType() != NotificationType.OK)
                    .sorted(Comparator.comparing(o -> ((UserNotification) o).getPage().getUpdatedOn()).reversed())
                    .collect(Collectors.toList());

            if (!usefulNotifications.isEmpty()) {
                try {
                    notifyUser(user, usefulNotifications);
                } catch (IOException e) {
                    LOGGER.error("Failed to notify user {}.", user.getInitials(), e);
                }
            }
        });
    }

    protected abstract void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) throws IOException;
}
