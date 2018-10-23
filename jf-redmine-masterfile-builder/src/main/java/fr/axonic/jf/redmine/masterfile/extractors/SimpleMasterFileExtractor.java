package fr.axonic.jf.redmine.masterfile.extractors;

import fr.axonic.jf.engine.InstantiatedStep;
import fr.axonic.jf.engine.JustificationMatrix;
import fr.axonic.jf.engine.pattern.JustificationStep;
import fr.axonic.jf.engine.pattern.Pattern;
import fr.axonic.jf.engine.support.Support;
import fr.axonic.jf.instance.redmine.RedmineDocument;
import fr.axonic.jf.instance.redmine.RedmineDocumentEvidence;
import fr.axonic.jf.redmine.masterfile.MasterFile;
import fr.axonic.jf.redmine.masterfile.RedmineMapperProvider;
import javafx.util.Pair;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class SimpleMasterFileExtractor implements MasterFileExtractor {

    private static final String TECHNICAL_SPECIFICATIONS_ID = "REDMINE";

    private final String jfServiceUrl;
    private final String projectName;

    public SimpleMasterFileExtractor(String jfServiceUrl, String projectName) {
        this.jfServiceUrl = jfServiceUrl;
        this.projectName = projectName;
    }

    @Override
    public MasterFile extract() throws IOException {
        return getMatrix().map(this::translate).orElse(null);
    }

    private MasterFile translate(JustificationMatrix matrix) {
        MasterFile masterFile = new MasterFile(getProjectName());

        for (InstantiatedStep row : matrix.getContent()) {
            List<RedmineDocument> recipient = masterFile.getDevelopmentDocuments(); // TODO Redirect in the correct list

            row.getStep().getSupports().stream()
                    .filter(s -> s instanceof RedmineDocumentEvidence)
                    .map(s -> (RedmineDocumentEvidence) s)
                    .map(Support::getElement)
                    .forEach(recipient::add);
        }

        return masterFile;
    }

    private String getProjectName() {
        return projectName;
    }

    private Optional<JustificationMatrix> getMatrix() throws IOException {
        URL url = new URL(jfServiceUrl + "/" + TECHNICAL_SPECIFICATIONS_ID + "/matrix");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Content-Type", "application/json");

        if (connection.getResponseCode() == Response.Status.FOUND.getStatusCode()) {
            return Optional.ofNullable(new RedmineMapperProvider().getContext(null).readValue(connection.getInputStream(), JustificationMatrix.class));
        } else {
            return Optional.empty();
        }
    }
}
