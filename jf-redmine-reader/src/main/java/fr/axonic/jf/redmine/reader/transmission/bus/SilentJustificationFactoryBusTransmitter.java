package fr.axonic.jf.redmine.reader.transmission.bus;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.TransmittedSupports;

import java.util.List;

public class SilentJustificationFactoryBusTransmitter extends JustificationFactoryBusTransmitter {

    public SilentJustificationFactoryBusTransmitter(RedmineSupportsTranslator translator) {
        super(translator);
    }

    @Override
    public void send(List<JustificationDocument> approvals) {
        // Do nothing.
    }

    @Override
    protected void sendSupports(TransmittedSupports supports) {
        // Do nothing.
    }
}
