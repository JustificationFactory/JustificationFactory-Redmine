package fr.axonic.avek.redmine.processes.transmission;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.engine.support.evidence.Evidence;
import fr.axonic.avek.redmine.io.models.ConfigurationDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AvekBusCommunicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvekBusCommunicator.class);

    private final RedmineSupportsTranslator translator;
    private final ConfigurationDocument configuration;

    public AvekBusCommunicator(RedmineSupportsTranslator translator, ConfigurationDocument configuration) {
        this.translator = translator;
        this.configuration = configuration;
    }

    public void sendToBus(List<WikiPage> pages) throws IOException {
        List<Evidence> evidences = pages.stream()
                .flatMap(p -> Stream.of(translator.translateEvidence(p), translator.translateApproval(p)))
                .collect(Collectors.toList());

        URL url = new URL(configuration.getBusUrl() + "/supports");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream wr = new DataOutputStream(connection.getOutputStream());

        RedmineMapperProvider.getMapper().writeValue(wr, evidences);
        wr.flush();
        wr.close();

        LOGGER.info("Response code from bus: {}", connection.getResponseCode());
        connection.disconnect();
    }
}
