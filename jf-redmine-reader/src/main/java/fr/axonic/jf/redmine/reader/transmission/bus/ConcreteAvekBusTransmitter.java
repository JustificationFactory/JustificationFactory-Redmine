package fr.axonic.jf.redmine.reader.transmission.bus;

import fr.axonic.jf.redmine.reader.configuration.ConfigurationDocument;
import fr.axonic.jf.redmine.reader.transmission.RedmineMapperProvider;
import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.TransmittedSupports;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class ConcreteAvekBusTransmitter extends AvekBusTransmitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteAvekBusTransmitter.class);

    private final ConfigurationDocument configuration;

    public ConcreteAvekBusTransmitter(RedmineSupportsTranslator translator, ConfigurationDocument configuration) {
        super(translator);
        this.configuration = configuration;
    }

    protected void sendSupports(TransmittedSupports supports) throws IOException {
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
