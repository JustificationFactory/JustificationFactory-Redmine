package fr.axonic.avek.redmine.analysis.reporting;

import fr.axonic.avek.redmine.utils.FreemarkerConfiguration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class AnalysisFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisFormatter.class);

    public String formatHtml(AnalysisReport report) throws IOException {
        Template template = FreemarkerConfiguration.getConfiguration().getTemplate("ranking-wiki-html.ftl");

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            template.process(report, new OutputStreamWriter(bytes));

            return new String(bytes.toByteArray(), Charset.defaultCharset());
        } catch (TemplateException e) {
            LOGGER.error("Unexpected template exception", e);

            return null;
        }
    }
}
