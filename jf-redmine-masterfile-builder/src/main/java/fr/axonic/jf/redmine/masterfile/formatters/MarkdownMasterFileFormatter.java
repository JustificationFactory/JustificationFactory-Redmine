package fr.axonic.jf.redmine.masterfile.formatters;

import fr.axonic.jf.redmine.masterfile.MasterFile;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class MarkdownMasterFileFormatter implements MasterFileFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownMasterFileFormatter.class);
    private static Configuration freemarkerConfiguration;

    static {
        freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_28);

        freemarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(
                MarkdownMasterFileFormatter.class,
                "/templates"));

        freemarkerConfiguration.setDefaultEncoding("UTF-8");
        freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfiguration.setLogTemplateExceptions(false);
        freemarkerConfiguration.setWrapUncheckedExceptions(true);
    }

    public String format(MasterFile masterFile) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            Template template = freemarkerConfiguration.getTemplate("markdown-master-file.ftl");
            template.process(masterFile, new OutputStreamWriter(bytes));

            return new String(bytes.toByteArray(), Charset.defaultCharset());
        } catch (IOException | TemplateException e) {
            LOGGER.error("Unexpected template exception", e);
            e.printStackTrace();

            return null;
        }
    }
}
