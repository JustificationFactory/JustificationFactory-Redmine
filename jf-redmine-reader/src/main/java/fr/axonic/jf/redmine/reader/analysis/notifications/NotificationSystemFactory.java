package fr.axonic.jf.redmine.reader.analysis.notifications;

import fr.axonic.jf.redmine.reader.analysis.notifications.implementations.LoggerNotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.implementations.MailNotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.implementations.SilentNotificationSystem;
import fr.axonic.jf.redmine.reader.configuration.ConfigurationDocument;
import fr.axonic.jf.redmine.reader.configuration.NotifierType;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.utils.MailSender;

public class NotificationSystemFactory {

    private static final NotificationSystemFactory INSTANCE = new NotificationSystemFactory();

    public static NotificationSystemFactory getInstance() {
        return INSTANCE;
    }

    private NotificationSystemFactory() {
        // Singleton.
    }

    public NotificationSystem create(NotifierType type, ConfigurationDocument configuration, ProjectStatus project) {
        switch (type) {
            case MAIL:
                return new MailNotificationSystem(new MailSender(configuration), configuration.getRedmineUrl(), project);
            case LOGGER:
                return new LoggerNotificationSystem();
            case SILENT:
            default:
                return new SilentNotificationSystem();
        }
    }
}
