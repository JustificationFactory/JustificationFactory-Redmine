package fr.axonic.avek.redmine.processes;

import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.avek.redmine.io.models.ProjectsDocument;
import fr.axonic.avek.redmine.models.ValidationDocument;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectWikiProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectWikiProcessor.class);

    private final RedmineManager redmine;
    private final ValidationExtractor validationExtractor;
    private final VerifiersNotifier notifier;

    public ProjectWikiProcessor(RedmineManager redmine, ValidationExtractor validationExtractor, VerifiersNotifier notifier) {
        this.redmine = redmine;
        this.validationExtractor = validationExtractor;
        this.notifier = notifier;
    }

    public void processWiki(ProjectsDocument.ProjectStatus status) throws RedmineException {
        List<WikiPage> pages = getUpdatedWikiPages(status);

        LOGGER.info("Fetched {} wiki pages.", pages.size());

        Map<WikiPage, WikiPageDetail> details = new HashMap<>();
        for (WikiPage page : pages) {
            try {
                details.put(page, redmine.getWikiManager().getWikiPageDetailByProjectAndTitle(status.getProjectName(), page.getTitle()));
            } catch (NotFoundException e) {
                LOGGER.error("Could not fetch the details of page `{}`.", page.getTitle(), e);
            }
        }

        LOGGER.info("Fetched details of {} pages.", details.size());

        List<ValidationDocument> validations = generateValidationDocuments(details);

        LOGGER.info("Generated {} validation documents.", validations.size());

        List<ValidationDocument> readyValidationsDocuments = filterValidationsAndNotify(validations);

        LOGGER.info("Ready to send {} documents and their validations.", readyValidationsDocuments.size());
    }

    private List<WikiPage> getUpdatedWikiPages(ProjectsDocument.ProjectStatus status) throws RedmineException {
        List<WikiPage> pages = redmine.getWikiManager().getWikiPagesByProject(status.getProjectName());

        if (status.getLastExecutionTime() == null) {
            return pages;
        }

        return pages.stream()
                .filter(p -> LocalDateTime.ofInstant(p.getUpdatedOn().toInstant(), ZoneId.systemDefault()).isAfter(status.getLastExecutionTime()))
                .collect(Collectors.toList());
    }

    private List<ValidationDocument> generateValidationDocuments(Map<WikiPage, WikiPageDetail> sourcePagesDetails) {
        List<ValidationDocument> documents = new ArrayList<>();
        sourcePagesDetails.forEach((page, detail) -> validationExtractor.extractValidation(page, detail).ifPresent(documents::add));

        return documents;
    }

    private List<ValidationDocument> filterValidationsAndNotify(List<ValidationDocument> validations) {
        List<ValidationDocument> filtered = new ArrayList<>();
        ValidationChecker verifier = new ValidationChecker(notifier);

        for (ValidationDocument document : validations) {
            if (verifier.verify(document)) {
                filtered.add(document);
            }
        }

        notifier.processNotifications();

        return filtered;
    }
}
