package fr.axonic.avek.redmine.processes.implementations;

import fr.axonic.avek.redmine.io.communication.IdentityBinder;
import fr.axonic.avek.redmine.models.UserIdentity;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotification;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggerVerifiersNotifier extends VerifiersNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerVerifiersNotifier.class);

    public LoggerVerifiersNotifier(IdentityBinder identityBinder) {
        super(identityBinder);
    }

    @Override
    protected void processUserNotifications(UserIdentity identity, List<VerifiersNotification> notifications) {
        LOGGER.info("For {}:", identity.getInitials());

        notifications.forEach(n -> LOGGER.info("{} :: {} :: {}", identity.getInitials(), n.getPage().getTitle(), n.getTopic()));
    }
}
