package fr.axonic.avek.redmine.reader.analysis.notifications;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.redmine.reader.users.UserIdentity;

public class UserNotification {

    private final UserIdentity user;
    private final WikiPage page;
    private final NotificationType type;

    public UserNotification(UserIdentity user, WikiPage page, NotificationType type) {
        this.user = user;
        this.page = page;
        this.type = type;
    }

    public UserIdentity getUser() {
        return user;
    }

    public WikiPage getPage() {
        return page;
    }

    public NotificationType getType() {
        return type;
    }
}
