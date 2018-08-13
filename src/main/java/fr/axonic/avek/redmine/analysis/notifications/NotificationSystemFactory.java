package fr.axonic.avek.redmine.analysis.notifications;

import fr.axonic.avek.redmine.analysis.notifications.implementations.LoggerNotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.implementations.MailNotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.implementations.SilentNotificationSystem;
import fr.axonic.avek.redmine.configuration.ConfigurationDocument;
import fr.axonic.avek.redmine.configuration.NotifierType;
import fr.axonic.avek.redmine.configuration.ProjectStatus;
import fr.axonic.avek.redmine.utils.MailSender;

import java.time.LocalDateTime;

public class NotificationSystemFactory {

    private static final NotificationSystemFactory INSTANCE = new NotificationSystemFactory();

    public static NotificationSystemFactory getInstance() {
        return INSTANCE;
    }

    private NotificationSystemFactory() {
        // Singleton.
    }

    public NotificationSystem create(NotifierType type, LocalDateTime minimumNotifiableDate, ConfigurationDocument configuration, ProjectStatus project) {
        switch (type) {
            case MAIL:
                return new MailNotificationSystem(minimumNotifiableDate, new MailSender(configuration), configuration.getRedmineUrl(), project);
            case LOGGER:
                return new LoggerNotificationSystem(minimumNotifiableDate);
            case SILENT:
            default:
                return new SilentNotificationSystem();
        }
    }
}
