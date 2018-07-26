package fr.axonic.avek.redmine.processes.notifications;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.redmine.models.UserIdentity;

public class VerifiersNotification {

    private final NotificationTopic topic;
    private final UserIdentity user;
    private final WikiPage page;

    public VerifiersNotification(NotificationTopic topic, UserIdentity user, WikiPage page) {
        this.topic = topic;
        this.user = user;
        this.page = page;
    }

    public NotificationTopic getTopic() {
        return topic;
    }

    public UserIdentity getUser() {
        return user;
    }

    public WikiPage getPage() {
        return page;
    }
}
