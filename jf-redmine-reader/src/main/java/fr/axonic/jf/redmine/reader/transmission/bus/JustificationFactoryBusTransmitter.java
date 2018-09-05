package fr.axonic.jf.redmine.reader.transmission.bus;

import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.TransmittedSupports;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class JustificationFactoryBusTransmitter {

    private final RedmineSupportsTranslator translator;

    public JustificationFactoryBusTransmitter(RedmineSupportsTranslator translator) {
        this.translator = translator;
    }

    public void send(List<ApprovalDocument> approvals) throws IOException {
        TransmittedSupports supports = new TransmittedSupports();
        supports.setSupports(approvals.stream()
                .flatMap(p -> Stream.of(translator.translateEvidence(p), translator.translateApproval(p)))
                .collect(Collectors.toList()));

        sendSupports(supports);
    }

    protected abstract void sendSupports(TransmittedSupports supports) throws IOException;
}
