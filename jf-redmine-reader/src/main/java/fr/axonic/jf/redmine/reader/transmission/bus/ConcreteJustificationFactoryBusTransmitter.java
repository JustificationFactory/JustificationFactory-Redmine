package fr.axonic.jf.redmine.reader.transmission.bus;

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

public class ConcreteJustificationFactoryBusTransmitter extends JustificationFactoryBusTransmitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteJustificationFactoryBusTransmitter.class);

    private final String justificationFactoryBusUrl;

    public ConcreteJustificationFactoryBusTransmitter(RedmineSupportsTranslator translator, String justificationFactoryBusUrl) {
        super(translator);
        this.justificationFactoryBusUrl = justificationFactoryBusUrl;
    }

    @Override
    protected void sendSupports(TransmittedSupports supports) throws IOException {
        URL url = new URL(justificationFactoryBusUrl + "/supports");

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
