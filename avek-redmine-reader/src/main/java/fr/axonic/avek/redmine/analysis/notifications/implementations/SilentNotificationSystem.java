package fr.axonic.avek.redmine.analysis.notifications.implementations;

import fr.axonic.avek.redmine.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.UserNotification;
import fr.axonic.avek.redmine.users.UserIdentity;

import java.util.List;

public class SilentNotificationSystem extends NotificationSystem {

    @Override
    protected void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) {
        // Nothing here.
    }
}
