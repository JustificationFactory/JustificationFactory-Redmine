package fr.axonic.jf.redmine.masterfile.extractors;

import fr.axonic.avek.engine.pattern.JustificationStep;
import fr.axonic.avek.engine.pattern.Pattern;
import fr.axonic.avek.engine.support.Support;
import fr.axonic.avek.instance.redmine.RedmineDocument;
import fr.axonic.avek.instance.redmine.RedmineDocumentEvidence;
import fr.axonic.jf.redmine.masterfile.MasterFile;
import javafx.util.Pair;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class SimpleMasterFileExtractor implements MasterFileExtractor {

    private final String jfServiceUrl;
    private final String justificationSystemId;

    public SimpleMasterFileExtractor(String jfServiceUrl, String justificationSystemId) {
        this.jfServiceUrl = jfServiceUrl;
        this.justificationSystemId = justificationSystemId;
    }

    @Override
    public MasterFile extract() throws IOException {
        return getMatrix().map(this::translate).orElse(null);
    }

    private MasterFile translate(List<Pair<Pattern,JustificationStep>> matrix) {
        MasterFile masterFile = new MasterFile(getProjectName());

        for (Pair<Pattern, JustificationStep> row : matrix) {
            List<RedmineDocument> recipient = masterFile.getDevelopmentDocuments(); // TODO Redirect in the correct list

            row.getValue().getSupports().stream()
                    .filter(s -> s instanceof RedmineDocumentEvidence)
                    .map(s -> (RedmineDocumentEvidence) s)
                    .map(Support::getElement)
                    .forEach(recipient::add);
        }

        return masterFile;
    }

    private String getProjectName() {
        return justificationSystemId;
    }

    private Optional<List<Pair<Pattern,JustificationStep>>> getMatrix() throws IOException {
        URL url = new URL(jfServiceUrl + "/" + justificationSystemId + "/matrix");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Content-Type", "application/json");

        if (connection.getResponseCode() == Response.Status.FOUND.getStatusCode()) {
            System.out.println(connection.getContent());
            // TODO Here.
            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }
}
