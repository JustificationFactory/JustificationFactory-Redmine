package fr.axonic.jf.redmine.reader.transmission.bus;

import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.TransmittedSupports;

import java.util.List;

public class SilentAvekBusTransmitter extends AvekBusTransmitter {

    public SilentAvekBusTransmitter(RedmineSupportsTranslator translator) {
        super(translator);
    }

    @Override
    public void send(List<ApprovalDocument> approvals) {
        // Do nothing.
    }

    @Override
    protected void sendSupports(TransmittedSupports supports) {
        // Do nothing.
    }
}
