package fr.axonic.avek.redmine.transmission.bus;

import fr.axonic.avek.redmine.transmission.RedmineSupportsTranslator;
import fr.axonic.avek.redmine.transmission.TransmittedSupports;

public class SilentAvekBusTransmitter extends AvekBusTransmitter {

    public SilentAvekBusTransmitter(RedmineSupportsTranslator translator) {
        super(translator);
    }

    @Override
    protected void sendSupports(TransmittedSupports supports) {
        // Do nothing.
    }
}
