package fr.axonic.jf.redmine.reader.configuration;

import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.bus.AvekBusTransmitter;
import fr.axonic.jf.redmine.reader.transmission.bus.ConcreteAvekBusTransmitter;
import fr.axonic.jf.redmine.reader.transmission.bus.SilentAvekBusTransmitter;

public class AvekBusTransmitterFactory {

    private static final AvekBusTransmitterFactory INSTANCE = new AvekBusTransmitterFactory();

    public static AvekBusTransmitterFactory getInstance() {
        return INSTANCE;
    }

    private AvekBusTransmitterFactory() {
        // Singleton.
    }

    public AvekBusTransmitter create(AvekBusTransmitterType type, ConfigurationDocument configuration, ProjectStatus project) {
        RedmineSupportsTranslator translator = new RedmineSupportsTranslator(configuration.getRedmineCredentials(), project);
        switch (type) {
            case CONCRETE:
                return new ConcreteAvekBusTransmitter(translator, configuration.getJustificationFactoryBusUrl());
            case SILENT:
            default:
                return new SilentAvekBusTransmitter(translator);
        }
    }
}
