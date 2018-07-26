package fr.axonic.avek.redmine.processes;

import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.avek.redmine.models.ValidationDocument;

import java.util.Optional;

public interface ValidationExtractor {

    Optional<ValidationDocument> extractValidation(WikiPage wikiPage, WikiPageDetail pageDetail);
}
