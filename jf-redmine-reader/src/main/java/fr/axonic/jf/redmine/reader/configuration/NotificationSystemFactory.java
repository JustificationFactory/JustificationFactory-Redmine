package fr.axonic.jf.redmine.reader.configuration;

import fr.axonic.jf.redmine.reader.analysis.notifications.NotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.LoggerNotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.MailNotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.SilentNotificationSystem;
import fr.axonic.jf.redmine.reader.users.bindings.ProjectIdentityBinder;
import fr.axonic.jf.redmine.reader.utils.MailSender;

public class NotificationSystemFactory {

    private static final NotificationSystemFactory INSTANCE = new NotificationSystemFactory();

    public static NotificationSystemFactory getInstance() {
        return INSTANCE;
    }

    private NotificationSystemFactory() {
        // Singleton.
    }

    public NotificationSystem create(NotifierType type, ConfigurationDocument configuration, ProjectStatus project, ProjectIdentityBinder identityBinder) {
        switch (type) {
            case MAIL:
                return new MailNotificationSystem(identityBinder, new MailSender(configuration.getEmailCredentials()), configuration.getRedmineCredentials().getUrl(), project);
            case LOGGER:
                return new LoggerNotificationSystem(identityBinder);
            case SILENT:
            default:
                return new SilentNotificationSystem();
        }
    }
}
