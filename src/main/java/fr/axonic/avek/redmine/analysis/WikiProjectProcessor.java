package fr.axonic.avek.redmine.analysis;

import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.avek.redmine.analysis.approvals.ApprovalDocument;
import fr.axonic.avek.redmine.analysis.approvals.extraction.ApprovalExtractor;
import fr.axonic.avek.redmine.analysis.approvals.verification.ApprovalVerifier;
import fr.axonic.avek.redmine.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.implementations.SilentNotificationSystem;
import fr.axonic.avek.redmine.analysis.reporting.AnalysisReport;
import fr.axonic.avek.redmine.configuration.ConfigurationDocument;
import fr.axonic.avek.redmine.configuration.ProjectStatus;
import fr.axonic.avek.redmine.transmission.RedmineSupportsTranslator;
import fr.axonic.avek.redmine.transmission.bus.AvekBusTransmitter;
import fr.axonic.avek.redmine.transmission.bus.SilentAvekBusTransmitter;
import fr.axonic.avek.redmine.users.bindings.IdentityBinder;
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
    private final AvekBusTransmitter transmitter;
    private final IdentityBinder identityBinder;
    private final ProjectStatus studiedProject;
    private final LocalDateTime minimumVerificationDate;

    public WikiProjectProcessor(RedmineManager redmine,
                                ApprovalExtractor approvalExtractor,
                                NotificationSystem notifier,
                                AvekBusTransmitter transmitter,
                                IdentityBinder identityBinder,
                                ProjectStatus studiedProject, LocalDateTime minimumVerificationDate) {
        this.redmine = redmine;
        this.approvalExtractor = approvalExtractor;
        this.notifier = notifier;
        this.transmitter = transmitter;
        this.identityBinder = identityBinder;
        this.studiedProject = studiedProject;
        this.minimumVerificationDate = minimumVerificationDate;
    }

    public AnalysisReport runAnalysis() throws RedmineException, IOException {
        AnalysisReport report = new AnalysisReport();

        List<WikiPage> projectPages = redmine.getWikiManager().getWikiPagesByProject(studiedProject.getProjectName());

        LOGGER.info("Fetched {} wiki pages.", projectPages.size());

        Map<WikiPage, WikiPageDetail> pagesDetails = new HashMap<>();
        for (WikiPage page : projectPages) {
            try {
                pagesDetails.put(page, redmine.getWikiManager().getWikiPageDetailByProjectAndTitle(studiedProject.getProjectName(), page.getTitle()));
            } catch (NotFoundException e) {
                LOGGER.error("Could not fetch the details of page `{}`.", page.getTitle(), e);
            }
        }

        LOGGER.info("Fetched details of {} pages.", pagesDetails.size());
        report.setTotalWikiPages(pagesDetails.size());


        List<ApprovalDocument> generatedApprovals = pagesDetails.entrySet().stream()
                .map(tuple -> approvalExtractor.extract(tuple.getKey(), tuple.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        LOGGER.info("Built {} pages approvals.", generatedApprovals.size());
        report.setWikiPagesWithApproval(generatedApprovals.size());

        ApprovalVerifier approvalVerifier = new ApprovalVerifier(minimumVerificationDate, notifier, identityBinder, report);

        List<ApprovalDocument> validApprovals = generatedApprovals.stream()
                .filter(approval -> {
                    boolean isValid = approvalVerifier.verify(approval);

                    report.acknowledge(approval, isValid);

                    return isValid;
                })
                .collect(Collectors.toList());

        LOGGER.info("Built and validated {} pages approvals.", validApprovals.size());

        notifier.notifyUsers();

        transmitter.send(validApprovals);

        return report;
    }

    public static Builder builder(ConfigurationDocument runConfiguration) {
        return new Builder(runConfiguration);
    }

    public static class Builder {

        private final ConfigurationDocument runConfiguration;
        private ApprovalExtractor approvalExtractor;
        private NotificationSystem notifier;
        private AvekBusTransmitter transmitter;
        private IdentityBinder identityBinder;
        private LocalDateTime minimumVerificationDate;

        Builder(ConfigurationDocument runConfiguration) {
            this.runConfiguration = runConfiguration;
        }

        public Builder with(ApprovalExtractor approvalExtractor) {
            this.approvalExtractor = approvalExtractor;

            return this;
        }

        public Builder with(NotificationSystem notifier) {
            this.notifier = notifier;

            return this;
        }

        public Builder with(AvekBusTransmitter transmitter) {
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

        public WikiProjectProcessor forProject(ProjectStatus status) {
            Objects.requireNonNull(approvalExtractor);
            Objects.requireNonNull(identityBinder);
            Objects.requireNonNull(status);

            RedmineManager redmine = RedmineManagerFactory.createWithApiKey(runConfiguration.getRedmineUrl(), runConfiguration.getRedmineApiKey());

            if (notifier == null) {
                notifier = new SilentNotificationSystem();
            }

            if (transmitter == null) {
                transmitter = new SilentAvekBusTransmitter(new RedmineSupportsTranslator(runConfiguration, status));
            }

            if (minimumVerificationDate == null) {
                minimumVerificationDate = LocalDateTime.MIN;
            }

            return new WikiProjectProcessor(redmine, approvalExtractor, notifier, transmitter, identityBinder, status, minimumVerificationDate);
        }
    }
}
