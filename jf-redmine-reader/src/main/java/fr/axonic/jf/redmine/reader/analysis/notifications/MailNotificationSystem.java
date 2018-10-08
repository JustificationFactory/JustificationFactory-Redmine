package fr.axonic.jf.redmine.reader.analysis.notifications;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalIssue;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalIssueLevel;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.users.UserIdentity;
import fr.axonic.jf.redmine.reader.users.bindings.ProjectIdentityBinder;
import fr.axonic.jf.redmine.reader.utils.FreemarkerConfiguration;
import fr.axonic.jf.redmine.reader.utils.MailSender;
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

    public MailNotificationSystem(ProjectIdentityBinder identityBinder, MailSender sender, String redmineUrl, ProjectStatus project) {
        super(identityBinder);
        this.sender = sender;
        this.redmineUrl = redmineUrl;
        this.project = project;
    }

    @Override
    protected void notifyProjectManager(UserIdentity user, List<ApprovalIssue> userIssues, List<ApprovalIssue> otherIssues, List<JustificationDocument> validatedDocuments) throws IOException {
        Map<String, Object> tree = new HashMap<>();
        tree.put("user", user);
        tree.put("projectName", project.getProjectName());
        tree.put("errors", userIssues.stream().filter(i -> i.getIssueType().getLevel() == ApprovalIssueLevel.ERROR).collect(Collectors.toList()));
        tree.put("warnings", userIssues.stream().filter(i -> i.getIssueType().getLevel() == ApprovalIssueLevel.WARNING).collect(Collectors.toList()));
        tree.put("redmineUrl", redmineUrl);
        tree.put("others", otherIssues);
        tree.put("validated", validatedDocuments);

        Template template = FreemarkerConfiguration.getConfiguration().getTemplate("project-manager-email.ftl");

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            template.process(tree, new OutputStreamWriter(bytes));

            String content = new String(bytes.toByteArray(), Charset.defaultCharset());

            sender.sendEmail(user, "Redmine Wiki report", content);
        } catch (TemplateException e) {
            LOGGER.error("Unexpected template exception", e);
        } catch (MessagingException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void notifyUser(UserIdentity user, List<ApprovalIssue> userIssues) throws IOException {
        Map<String, Object> tree = new HashMap<>();
        tree.put("user", user);
        tree.put("projectName", project.getProjectName());
        tree.put("errors", userIssues.stream().filter(i -> i.getIssueType().getLevel() == ApprovalIssueLevel.ERROR).collect(Collectors.toList()));
        tree.put("warnings", userIssues.stream().filter(i -> i.getIssueType().getLevel() == ApprovalIssueLevel.WARNING).collect(Collectors.toList()));
        tree.put("redmineUrl", redmineUrl);

        Template template = FreemarkerConfiguration.getConfiguration().getTemplate("verifiers-email.ftl");

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            template.process(tree, new OutputStreamWriter(bytes));

            String content = new String(bytes.toByteArray(), Charset.defaultCharset());

            sender.sendEmail(user, "Redmine Wiki issues", content);
        } catch (TemplateException e) {
            LOGGER.error("Unexpected template exception", e);
        } catch (MessagingException e) {
            throw new IOException(e);
        }
    }
}
