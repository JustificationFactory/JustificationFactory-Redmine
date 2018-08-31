package fr.axonic.jf.redmine.reader.analysis.notifications;

public enum NotificationType {

    MISSING_DATE(NotificationLevel.ERROR),
    WRONG_DATE(NotificationLevel.WARNING),
    NOT_SIGNED_AS_AUTHOR(NotificationLevel.WARNING),
    NOT_SIGNED_AS_VERIFIER(NotificationLevel.ERROR),
    NO_AUTHOR(NotificationLevel.ERROR),
    OK(NotificationLevel.OK);

    private final NotificationLevel level;

    NotificationType(NotificationLevel level) {
        this.level = level;
    }

    public NotificationLevel getLevel() {
        return level;
    }
}
