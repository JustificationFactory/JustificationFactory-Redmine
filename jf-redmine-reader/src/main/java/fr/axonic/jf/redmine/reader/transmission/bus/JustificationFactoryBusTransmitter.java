package fr.axonic.jf.redmine.reader.transmission.bus;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
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

    public void send(List<JustificationDocument> justificationDocuments) throws IOException {
        TransmittedSupports supports = new TransmittedSupports();
        supports.setSupports(justificationDocuments.stream()
                .flatMap(j -> Stream.of(translator.translateEvidence(j.getApproval()), translator.translateApproval(j.getApproval())))
                .collect(Collectors.toList()));

        sendSupports(supports);
    }

    protected abstract void sendSupports(TransmittedSupports supports) throws IOException;
}
