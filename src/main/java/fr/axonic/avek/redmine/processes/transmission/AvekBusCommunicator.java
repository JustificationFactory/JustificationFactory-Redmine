package fr.axonic.avek.redmine.processes.transmission;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.avek.engine.support.Support;
import fr.axonic.avek.redmine.io.models.ConfigurationDocument;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
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
        TransmittedSupports supports = new TransmittedSupports();
        supports.setSupports(pages.stream()
                .flatMap(p -> Stream.of(translator.translateEvidence(p), translator.translateApproval(p)))
                .collect(Collectors.toList()));

        URL url = new URL(configuration.getBusUrl() + "/supports");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json");

        OutputStream wr = new DataOutputStream(connection.getOutputStream());

        new RedmineMapperProvider().getContext(null).writeValue(wr, supports);
        wr.flush();
        wr.close();

        LOGGER.info("Response code from bus: {} ; message: {}", connection.getResponseCode(), IOUtils.toString(connection.getInputStream(), Charset.defaultCharset()));
        connection.disconnect();
    }
}
