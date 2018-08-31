package fr.axonic.jf.redmine.reader.transmission.bus;

import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.TransmittedSupports;

public class SilentAvekBusTransmitter extends AvekBusTransmitter {

    public SilentAvekBusTransmitter(RedmineSupportsTranslator translator) {
        super(translator);
    }

    @Override
    protected void sendSupports(TransmittedSupports supports) {
        // Do nothing.
    }
}
