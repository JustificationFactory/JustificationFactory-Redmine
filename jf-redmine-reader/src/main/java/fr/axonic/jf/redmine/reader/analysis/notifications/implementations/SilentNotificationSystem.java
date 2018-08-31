package fr.axonic.jf.redmine.reader.analysis.notifications.implementations;

import fr.axonic.jf.redmine.reader.analysis.notifications.NotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.UserNotification;
import fr.axonic.jf.redmine.reader.users.UserIdentity;

import java.util.List;

public class SilentNotificationSystem extends NotificationSystem {

    @Override
    protected void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) {
        // Nothing here.
    }
}
