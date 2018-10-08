package fr.axonic.jf.redmine.reader.analysis;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalDocumentAnalyzer;
import fr.axonic.jf.redmine.reader.analysis.approvals.analysis.ApprovalIssue;
import fr.axonic.jf.redmine.reader.analysis.approvals.extraction.ApprovalDocumentExtractor;
import fr.axonic.jf.redmine.reader.analysis.notifications.NotificationContent;
import fr.axonic.jf.redmine.reader.analysis.notifications.NotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.notifications.SilentNotificationSystem;
import fr.axonic.jf.redmine.reader.analysis.reporting.AnalysisReport;
import fr.axonic.jf.redmine.reader.configuration.ProjectConfiguration;
import fr.axonic.jf.redmine.reader.configuration.ProjectStatus;
import fr.axonic.jf.redmine.reader.configuration.RedmineCredentials;
import fr.axonic.jf.redmine.reader.configuration.RedmineDatabaseCredentials;
import fr.axonic.jf.redmine.reader.transmission.RedmineSupportsTranslator;
import fr.axonic.jf.redmine.reader.transmission.bus.JustificationFactoryBusTransmitter;
import fr.axonic.jf.redmine.reader.transmission.bus.SilentJustificationFactoryBusTransmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WikiProjectProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiProjectProcessor.class);

    private final RedmineManager redmine;
    private final LockedDocumentChecker lockedDocumentChecker;
    private final ApprovalDocumentExtractor approvalExtractor;
    private final NotificationSystem notifier;
    private final JustificationFactoryBusTransmitter transmitter;
    private final ProjectConfiguration projectConfiguration;
    private final ProjectStatus studiedProject;

    private WikiProjectProcessor(RedmineManager redmine,
                                LockedDocumentChecker lockedDocumentChecker,
                                ApprovalDocumentExtractor approvalExtractor,
                                NotificationSystem notifier,
                                JustificationFactoryBusTransmitter transmitter,
                                ProjectConfiguration projectConfiguration,
                                ProjectStatus studiedProject) {
        this.redmine = redmine;
        this.lockedDocumentChecker = lockedDocumentChecker;
        this.approvalExtractor = approvalExtractor;
        this.notifier = notifier;
        this.transmitter = transmitter;
        this.projectConfiguration = projectConfiguration;
        this.studiedProject = studiedProject;
    }

    public AnalysisReport runAnalysis() throws RedmineException, IOException {
        AnalysisReport report = new AnalysisReport();

        List<JustificationDocument> justificationDocuments = fetchJustificationDocuments();

        LOGGER.info("Fetched {} justification documents: {}.", justificationDocuments.size(), justificationDocuments.stream().map(d -> d.getAssociatedPage().getTitle()).collect(Collectors.toList()));
        report.setWikiPagesWithApproval(justificationDocuments.size());

        ApprovalDocumentAnalyzer approvalDocumentAnalyzer = new ApprovalDocumentAnalyzer();

        Map<JustificationDocument, List<ApprovalIssue>> justificationDocumentsToIssues = justificationDocuments.stream()
                .collect(Collectors.toMap(document -> document, document -> approvalDocumentAnalyzer.analyze(document.getApproval())));

        List<JustificationDocument> validatedDocuments = justificationDocumentsToIssues.entrySet().stream()
                .filter(n -> n.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        LOGGER.info("Built and validated {} pages approvals.", validatedDocuments.size());

        NotificationContent content = new NotificationContent();
        content.setListedIssuesInApproval(justificationDocumentsToIssues.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        content.setValidatedJustificationDocuments(validatedDocuments);

        notifier.notify(content);

        transmitter.send(validatedDocuments);

        return report;
    }

    private List<JustificationDocument> fetchJustificationDocuments() throws RedmineException {
        return redmine.getWikiManager().getWikiPagesByProject(studiedProject.getProjectName()).stream()
                .filter(wikiPage -> !projectConfiguration.getIgnoredDocuments().contains(wikiPage.getTitle()))
                .filter(wikiPage -> !lockedDocumentChecker.isLocked(wikiPage))
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

                    if (approval.isPresent()) {
                        ApprovalDocument realApproval = approval.get();

                        JustificationDocument justificationDocument = new JustificationDocument(wikiPage, detail, realApproval);
                        realApproval.setSource(justificationDocument);

                        return justificationDocument;
                    } else {
                        return new JustificationDocument(wikiPage, detail, null);
                    }
                })
                .filter(justificationDocument -> justificationDocument.getPageDetail() != null && justificationDocument.getApproval() != null)
                .collect(Collectors.toList());
    }

    public static Builder builder(RedmineCredentials redmineCredentials, RedmineDatabaseCredentials redmineDatabaseCredentials) {
        return new Builder(redmineCredentials, redmineDatabaseCredentials);
    }

    public static class Builder {

        private final RedmineCredentials redmineCredentials;
        private final RedmineDatabaseCredentials redmineDatabaseCredentials;
        private ApprovalDocumentExtractor approvalExtractor;
        private NotificationSystem notifier;
        private JustificationFactoryBusTransmitter transmitter;

        Builder(RedmineCredentials redmineCredentials, RedmineDatabaseCredentials redmineDatabaseCredentials) {
            this.redmineCredentials = redmineCredentials;
            this.redmineDatabaseCredentials = redmineDatabaseCredentials;
        }

        public Builder with(ApprovalDocumentExtractor approvalExtractor) {
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

        public WikiProjectProcessor forProject(ProjectConfiguration configuration, ProjectStatus status) {
            Objects.requireNonNull(approvalExtractor);
            Objects.requireNonNull(status);

            RedmineManager redmine = RedmineManagerFactory.createWithApiKey(redmineCredentials.getUrl(), redmineCredentials.getApiKey());

            if (notifier == null) {
                notifier = new SilentNotificationSystem();
            }

            if (transmitter == null) {
                transmitter = new SilentJustificationFactoryBusTransmitter(new RedmineSupportsTranslator(redmineCredentials, status));
            }

            return new WikiProjectProcessor(redmine, new LockedDocumentChecker(redmineDatabaseCredentials), approvalExtractor, notifier, transmitter, configuration, status);
        }
    }
}
