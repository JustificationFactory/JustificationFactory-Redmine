package fr.axonic.avek.redmine.processes;

import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.avek.redmine.RankingSingleton;
import fr.axonic.avek.redmine.Runner;
import fr.axonic.avek.redmine.io.models.ProjectsDocument;
import fr.axonic.avek.redmine.models.ValidationDocument;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotifier;
import fr.axonic.avek.redmine.processes.ranking.RankingWikiGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        RankingSingleton.rankingData = new RankingWikiGenerator.RankingWikiData();
        RankingSingleton.rankingData.setDate(LocalDate.now());
        RankingSingleton.rankingData.setProjectName(status.getProjectName());

        List<WikiPage> pages = redmine.getWikiManager().getWikiPagesByProject(status.getProjectName());

        LOGGER.info("Fetched {} wiki pages.", pages.size());
        RankingSingleton.rankingData.setTotalPages(pages.size());

        Map<WikiPage, WikiPageDetail> details = new HashMap<>();
        for (WikiPage page : pages) {
            try {
                details.put(page, redmine.getWikiManager().getWikiPageDetailByProjectAndTitle(status.getProjectName(), page.getTitle()));
            } catch (NotFoundException e) {
                LOGGER.error("Could not fetch the details of page `{}`.", page.getTitle(), e);
            }
        }

        LOGGER.info("Fetched details of {} pages.", details.size());
        RankingSingleton.rankingData.setNotFoundPages(pages.size() - details.size());

        List<ValidationDocument> validations = generateValidationDocuments(details);

        LOGGER.info("Generated {} validation documents.", validations.size());
        RankingSingleton.rankingData.setWithoutApprovalPages(details.size() - validations.size());
        RankingSingleton.rankingData.setNokStructurePages((int) validations.stream().filter(v -> v == ValidationDocument.INVALID_DOCUMENT).count());

        List<ValidationDocument> wellStructuredDocument = validations.stream()
                .filter(v -> v != ValidationDocument.INVALID_DOCUMENT)
                .collect(Collectors.toList());

        List<ValidationDocument> readyValidationsDocuments = filterValidationsAndNotify(wellStructuredDocument);

        LOGGER.info("Ready to send {} documents and their validations.", readyValidationsDocuments.size());
        RankingSingleton.rankingData.setNokContentPages(wellStructuredDocument.size() - readyValidationsDocuments.size());
        RankingSingleton.rankingData.setOkContentPages(readyValidationsDocuments.size());

        List<WikiPage> updatedPages = keepUpdatedPages(status, pages);
    }

    private List<WikiPage> keepUpdatedPages(ProjectsDocument.ProjectStatus status, List<WikiPage> pages) {
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
