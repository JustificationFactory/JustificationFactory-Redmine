package fr.axonic.jf.redmine.reader.analysis.approvals;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;

import java.util.ArrayList;
import java.util.List;

public class ApprovalDocument {

    private JustificationDocument source;
    private final List<ApprovalSignature> signatures;

    public ApprovalDocument() {
        signatures = new ArrayList<>();
    }

    public List<ApprovalSignature> getSignatures() {
        return signatures;
    }

    public void setSource(JustificationDocument source) {
        this.source = source;
    }

    public JustificationDocument getSource() {
        return source;
    }
}
