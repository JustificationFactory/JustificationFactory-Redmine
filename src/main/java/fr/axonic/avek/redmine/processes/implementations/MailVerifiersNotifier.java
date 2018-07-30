package fr.axonic.avek.redmine.processes.implementations;

import fr.axonic.avek.redmine.io.communication.IdentityBinder;
import fr.axonic.avek.redmine.io.communication.MailSender;
import fr.axonic.avek.redmine.models.UserIdentity;
import fr.axonic.avek.redmine.processes.FreemarkerConfiguration;
import fr.axonic.avek.redmine.processes.notifications.NotificationTopic;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotification;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotifier;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MailVerifiersNotifier extends VerifiersNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailVerifiersNotifier.class);

    private final MailSender sender;
    private final String redmineUrl;
    private String currentProject;

    public MailVerifiersNotifier(IdentityBinder identityBinder, String redmineUrl, MailSender sender) {
        super(identityBinder);
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
        tree.put("issues", notifications.stream().filter(n -> n.getTopic() != NotificationTopic.OK).collect(Collectors.toList()));
        tree.put("redmineUrl", redmineUrl);

        Template template = FreemarkerConfiguration.getConfiguration().getTemplate("verifiers-email.ftl");

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            template.process(tree, new OutputStreamWriter(bytes));

            String content = new String(bytes.toByteArray(), Charset.defaultCharset());

            sender.sendEmail(identity, "Redmine Wiki issues", content);
        } catch (TemplateException e) {
            LOGGER.error("Unexpected template exception", e);
        } catch (MessagingException e) {
            throw new IOException(e);
        }
    }
}
