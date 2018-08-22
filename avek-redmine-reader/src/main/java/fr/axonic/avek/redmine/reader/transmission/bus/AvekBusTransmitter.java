package fr.axonic.avek.redmine.reader.transmission.bus;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.avek.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.avek.redmine.reader.transmission.TransmittedSupports;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AvekBusTransmitter {

    private final RedmineSupportsTranslator translator;

    public AvekBusTransmitter(RedmineSupportsTranslator translator) {
        this.translator = translator;
    }

    public void send(List<ApprovalDocument> approvals) throws IOException {
        TransmittedSupports supports = new TransmittedSupports();
        supports.setSupports(approvals.stream()
                .map(ApprovalDocument::getWikiPage)
                .flatMap(p -> Stream.of(translator.translateEvidence(p), translator.translateApproval(p)))
                .collect(Collectors.toList()));

        sendSupports(supports);
    }

    public void sendToBus(List<WikiPage> pages) throws IOException {
        TransmittedSupports supports = new TransmittedSupports();
        supports.setSupports(pages.stream()
                .flatMap(p -> Stream.of(translator.translateEvidence(p), translator.translateApproval(p)))
                .collect(Collectors.toList()));

        sendSupports(supports);
    }

    protected abstract void sendSupports(TransmittedSupports supports) throws IOException;
}
