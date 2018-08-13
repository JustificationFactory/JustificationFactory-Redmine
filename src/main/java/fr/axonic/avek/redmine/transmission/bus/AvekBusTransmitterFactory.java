package fr.axonic.avek.redmine.transmission.bus;

import fr.axonic.avek.redmine.configuration.AvekBusTransmitterType;
import fr.axonic.avek.redmine.configuration.ConfigurationDocument;
import fr.axonic.avek.redmine.configuration.ProjectStatus;
import fr.axonic.avek.redmine.transmission.RedmineSupportsTranslator;

public class AvekBusTransmitterFactory {

    private static final AvekBusTransmitterFactory INSTANCE = new AvekBusTransmitterFactory();

    public static AvekBusTransmitterFactory getInstance() {
        return INSTANCE;
    }

    private AvekBusTransmitterFactory() {
        // Singleton.
    }

    public AvekBusTransmitter create(AvekBusTransmitterType type, ConfigurationDocument configuration, ProjectStatus project) {
        RedmineSupportsTranslator translator = new RedmineSupportsTranslator(configuration, project);
        switch (type) {
            case CONCRETE:
                return new ConcreteAvekBusTransmitter(translator, configuration);
            case SILENT:
            default:
                return new SilentAvekBusTransmitter(translator);
        }
    }
}
