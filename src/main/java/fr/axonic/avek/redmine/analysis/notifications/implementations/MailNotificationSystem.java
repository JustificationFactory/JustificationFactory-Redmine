package fr.axonic.avek.redmine.analysis.notifications.implementations;

import fr.axonic.avek.redmine.analysis.notifications.NotificationLevel;
import fr.axonic.avek.redmine.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.UserNotification;
import fr.axonic.avek.redmine.configuration.ProjectStatus;
import fr.axonic.avek.redmine.users.UserIdentity;
import fr.axonic.avek.redmine.utils.FreemarkerConfiguration;
import fr.axonic.avek.redmine.utils.MailSender;
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

public class MailNotificationSystem extends NotificationSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailNotificationSystem.class);

    private final MailSender sender;
    private final String redmineUrl;
    private final ProjectStatus project;

    public MailNotificationSystem(MailSender sender, String redmineUrl, ProjectStatus project) {
        this.sender = sender;
        this.redmineUrl = redmineUrl;
        this.project = project;
    }

    @Override
    protected void notifyUser(UserIdentity identity, List<UserNotification> accumulatedNotifications) throws IOException {
        Map<String, Object> tree = new HashMap<>();
        tree.put("user", identity);
        tree.put("projectName", project.getProjectName());
        tree.put("errors", accumulatedNotifications.stream().filter(n -> n.getType().getLevel() == NotificationLevel.ERROR).collect(Collectors.toList()));
        tree.put("warnings", accumulatedNotifications.stream().filter(n -> n.getType().getLevel() == NotificationLevel.WARNING).collect(Collectors.toList()));
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
