package fr.axonic.jf.redmine.reader.analysis;

import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.extraction.ApprovalExtractor;
import fr.axonic.jf.redmine.reader.analysis.approvals.verification.ApprovalVerifier;
import fr.axonic.jf.redmine.reader.analysis.notifications.NotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.implementations.SilentNotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.reporting.AnalysisReport;
import fr.axonic.jf.redmine.reader.configuration.ProjectConfiguration;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.configuration.RedmineCredentials;
import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.bus.JustificationFactoryBusTransmitter;
import fr.axonic.jf.redmine.reader.transmission.bus.SilentJustificationFactoryBusTransmitter;
import fr.axonic.jf.redmine.reader.users.bindings.IdentityBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class WikiProjectProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiProjectProcessor.class);

    private final RedmineManager redmine;
    private final ApprovalExtractor approvalExtractor;
    private final NotificationSystem notifier;
    private final JustificationFactoryBusTransmitter transmitter;
    private final IdentityBinder identityBinder;
    private final ProjectConfiguration projectConfiguration;
    private final ProjectStatus studiedProject;
    private final LocalDateTime minimumVerificationDate;

    public WikiProjectProcessor(RedmineManager redmine,
                                ApprovalExtractor approvalExtractor,
                                NotificationSystem notifier,
                                JustificationFactoryBusTransmitter transmitter,
                                IdentityBinder identityBinder,
                                ProjectConfiguration projectConfiguration,
                                ProjectStatus studiedProject,
                                LocalDateTime minimumVerificationDate) {
        this.redmine = redmine;
        this.approvalExtractor = approvalExtractor;
        this.notifier = notifier;
        this.transmitter = transmitter;
        this.identityBinder = identityBinder;
        this.projectConfiguration = projectConfiguration;
        this.studiedProject = studiedProject;
        this.minimumVerificationDate = minimumVerificationDate;
    }

    public AnalysisReport runAnalysis() throws RedmineException, IOException {
        AnalysisReport report = new AnalysisReport();

        List<JustificationDocument> justificationDocuments = fetchJustificationDocuments();

        LOGGER.info("Fetched {} justification documents: {}.", justificationDocuments.size(), justificationDocuments.stream().map(d -> d.getAssociatedPage().getTitle()).collect(Collectors.toList()));
        report.setWikiPagesWithApproval(justificationDocuments.size());

        ApprovalVerifier approvalVerifier = new ApprovalVerifier(minimumVerificationDate, notifier, identityBinder, report);
        List<JustificationDocument> validatedJustificationDocuments = justificationDocuments.stream()
                .filter(approval -> approvalVerifier.verify(approval.getApproval()))
                .sorted(Comparator.comparing(o -> o.getAssociatedPage().getUpdatedOn()))
                .collect(Collectors.toList());

        LOGGER.info("Built and validated {} pages approvals.", validatedJustificationDocuments.size());

        notifier.notifyUsers();

        transmitter.send(validatedJustificationDocuments);

        return report;
    }

    private List<JustificationDocument> fetchJustificationDocuments() throws RedmineException {
        return redmine.getWikiManager().getWikiPagesByProject(studiedProject.getProjectName()).stream()
                .filter(wikiPage -> !projectConfiguration.getIgnoredDocuments().contains(wikiPage.getTitle()))
                .map(wikiPage -> {
                    WikiPageDetail detail = null;
                    try {
                        detail = redmine.getWikiManager().getWikiPageDetailByProjectAndTitle(studiedProject.getProjectName(), wikiPage.getTitle());
                    } catch (RedmineException e) {
                        LOGGER.error("Could not fetch the details of page `{}`.", wikiPage.getTitle(), e);
                    }
                    
                    Optional<ApprovalDocument> approval = Optional.empty();
                    if (detail != null) {
                        approval = approvalExtractor.extract(wikiPage, detail);
                    }

                    return new JustificationDocument(wikiPage, detail, approval.orElse(null));
                })
                .filter(justificationDocument -> justificationDocument.getPageDetail() != null && justificationDocument.getApproval() != null)
                .collect(Collectors.toList());
    }

    public static Builder builder(RedmineCredentials redmineCredentials) {
        return new Builder(redmineCredentials);
    }

    public static class Builder {

        private final RedmineCredentials redmineCredentials;
        private ApprovalExtractor approvalExtractor;
        private NotificationSystem notifier;
        private JustificationFactoryBusTransmitter transmitter;
        private IdentityBinder identityBinder;
        private LocalDateTime minimumVerificationDate;

        Builder(RedmineCredentials redmineCredentials) {
            this.redmineCredentials = redmineCredentials;
        }

        public Builder with(ApprovalExtractor approvalExtractor) {
            this.approvalExtractor = approvalExtractor;

            return this;
        }

        public Builder with(NotificationSystem notifier) {
            this.notifier = notifier;

            return this;
        }

        public Builder with(JustificationFactoryBusTransmitter transmitter) {
            this.transmitter = transmitter;

            return this;
        }

        public Builder with(IdentityBinder identityBinder) {
            this.identityBinder = identityBinder;

            return this;
        }

        public Builder from(LocalDateTime minimumVerificationDate) {
            this.minimumVerificationDate = minimumVerificationDate;

            return this;
        }

        public WikiProjectProcessor forProject(ProjectConfiguration configuration, ProjectStatus status) {
            Objects.requireNonNull(approvalExtractor);
            Objects.requireNonNull(identityBinder);
            Objects.requireNonNull(status);

            RedmineManager redmine = RedmineManagerFactory.createWithApiKey(redmineCredentials.getUrl(), redmineCredentials.getApiKey());

            if (notifier == null) {
                notifier = new SilentNotificationSystem();
            }

            if (transmitter == null) {
                transmitter = new SilentJustificationFactoryBusTransmitter(new RedmineSupportsTranslator(redmineCredentials, status));
            }

            if (minimumVerificationDate == null) {
                minimumVerificationDate = LocalDateTime.MIN;
            }

            return new WikiProjectProcessor(redmine, approvalExtractor, notifier, transmitter, identityBinder, configuration, status, minimumVerificationDate);
        }
    }
}
