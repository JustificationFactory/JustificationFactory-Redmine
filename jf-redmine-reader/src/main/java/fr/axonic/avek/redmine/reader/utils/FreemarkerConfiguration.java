package fr.axonic.avek.redmine.reader.utils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerConfiguration {

    private static Configuration configuration;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_28);

        configuration.setTemplateLoader(new ClassTemplateLoader(
                FreemarkerConfiguration.class,
                "/templates"));

        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    private FreemarkerConfiguration() {
        // This is a singleton.
    }
}
