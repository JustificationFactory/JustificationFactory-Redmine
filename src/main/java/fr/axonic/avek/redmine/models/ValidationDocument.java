package fr.axonic.avek.redmine.models;

import com.taskadapter.redmineapi.bean.WikiPage;

import java.util.ArrayList;
import java.util.List;

public class ValidationDocument {

    private final WikiPage wikiPage;
    private final List<ValidationSignature> signatures;

    public ValidationDocument(WikiPage wikiPage) {
        this.wikiPage = wikiPage;
        signatures = new ArrayList<>();
    }

    public List<ValidationSignature> getSignatures() {
        return signatures;
    }

    public WikiPage getWikiPage() {
        return wikiPage;
    }
}
