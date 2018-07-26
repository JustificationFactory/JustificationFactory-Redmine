package fr.axonic.avek.redmine.processes.implementations;

import fr.axonic.avek.redmine.io.communication.MailSender;
import fr.axonic.avek.redmine.models.UserIdentity;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotification;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotifier;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailVerifiersNotifier extends VerifiersNotifier {

    private static Configuration configuration;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_28);

        configuration.setTemplateLoader(new ClassTemplateLoader(
                MailVerifiersNotifier.class,
                "/templates"));

        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
    }

    private final MailSender sender;
    private final String redmineUrl;
    private String currentProject;

    public MailVerifiersNotifier(String redmineUrl, MailSender sender) {
        this.redmineUrl = redmineUrl;
        this.sender = sender;
    }

    public void setCurrentProject(String currentProject) {
        this.currentProject = currentProject;
    }

    @Override
    protected void processUserNotifications(UserIdentity identity, List<VerifiersNotification> notifications) throws IOException {
        Map<String, Object> tree = new HashMap<>();
        tree.put("user", identity);
        tree.put("projectName", currentProject);
        tree.put("issues", notifications);
        tree.put("redmineUrl", redmineUrl);

        Template template = configuration.getTemplate("verifiers-email.ftl");

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            template.process(tree, new OutputStreamWriter(bytes));

            String content = new String(bytes.toByteArray(), Charset.defaultCharset());

            sender.sendEmail(identity, "Redmine Wiki issues", content);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            throw new IOException(e);
        }
    }
}
