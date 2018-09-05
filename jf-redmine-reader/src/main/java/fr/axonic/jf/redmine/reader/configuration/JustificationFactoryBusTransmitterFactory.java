package fr.axonic.jf.redmine.reader.configuration;

import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.bus.JustificationFactoryBusTransmitter;
import fr.axonic.jf.redmine.reader.transmission.bus.ConcreteJustificationFactoryBusTransmitter;
import fr.axonic.jf.redmine.reader.transmission.bus.SilentJustificationFactoryBusTransmitter;

public class JustificationFactoryBusTransmitterFactory {

    private static final JustificationFactoryBusTransmitterFactory INSTANCE = new JustificationFactoryBusTransmitterFactory();

    public static JustificationFactoryBusTransmitterFactory getInstance() {
        return INSTANCE;
    }

    private JustificationFactoryBusTransmitterFactory() {
        // Singleton.
    }

    public JustificationFactoryBusTransmitter create(JustificationFactoryBusTransmitterType type, ConfigurationDocument configuration, ProjectStatus project) {
        RedmineSupportsTranslator translator = new RedmineSupportsTranslator(configuration.getRedmineCredentials(), project);
        switch (type) {
            case CONCRETE:
                return new ConcreteJustificationFactoryBusTransmitter(translator, configuration.getJustificationFactoryBusUrl());
            case SILENT:
            default:
                return new SilentJustificationFactoryBusTransmitter(translator);
        }
    }
}
