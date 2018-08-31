package fr.axonic.jf.redmine.reader.transmission.bus;

import fr.axonic.jf.redmine.reader.configuration.AvekBusTransmitterType;
import fr.axonic.jf.redmine.reader.configuration.ConfigurationDocument;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;

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
