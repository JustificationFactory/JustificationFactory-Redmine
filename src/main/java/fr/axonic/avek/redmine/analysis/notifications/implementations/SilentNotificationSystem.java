package fr.axonic.avek.redmine.analysis.notifications.implementations;

import fr.axonic.avek.redmine.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.UserNotification;
import fr.axonic.avek.redmine.users.UserIdentity;

import java.time.LocalDateTime;
import java.util.List;

public class SilentNotificationSystem extends NotificationSystem {

    public SilentNotificationSystem() {
        super(LocalDateTime.MIN);
    }

    @Override
    protected void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) {
        // Nothing here.
    }
}
