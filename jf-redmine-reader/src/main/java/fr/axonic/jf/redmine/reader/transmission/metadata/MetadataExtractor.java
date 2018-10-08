package fr.axonic.jf.redmine.reader.transmission.metadata;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.jf.instance.redmine.RedmineDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;

public interface MetadataExtractor {

    RedmineDocument extractMetadata(WikiPage wikiPage);
    RedmineDocument extractMetadata(ApprovalDocument wikiPage);
}
