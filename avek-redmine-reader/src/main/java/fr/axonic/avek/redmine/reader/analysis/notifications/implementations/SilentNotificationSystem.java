package fr.axonic.avek.redmine.reader.analysis.notifications.implementations;

import fr.axonic.avek.redmine.reader.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.reader.analysis.notifications.UserNotification;
import fr.axonic.avek.redmine.reader.users.UserIdentity;

import java.util.List;

public class SilentNotificationSystem extends NotificationSystem {

    @Override
    protected void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) {
        // Nothing here.
    }
}
