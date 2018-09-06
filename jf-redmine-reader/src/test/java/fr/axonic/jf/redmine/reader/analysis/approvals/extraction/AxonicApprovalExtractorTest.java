package fr.axonic.jf.redmine.reader.analysis.approvals.extraction;

import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalSignature;
import fr.axonic.jf.redmine.reader.users.UserRole;
import fr.axonic.jf.redmine.reader.users.bindings.SimpleIdentityBinder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class AxonicApprovalExtractorTest {

    private ApprovalDocument document;
    private AxonicApprovalExtractor extractor;

    @Before
    public void initialize() {
        document = new ApprovalDocument(null);
        extractor = new AxonicApprovalExtractor(new SimpleIdentityBinder());
    }

    @Test
    public void shouldExtractSimpleCompleteValidation() {
        String content =
                "| Auteur : LW | Vérificateur : HC | Vérificateur : CD | \n" +
                "| Date : 05/06/18| Date : 05/06/18| Date : 06/06/18 | \n" +
                "| Acceptation numérique : OK--LW | Acceptation numérique :  OK--HC | Acceptation numérique :  OK--CD |";

        extractor.fillDocument(document, content);

        assertEquals(3, document.getSignatures().size());

        ApprovalSignature signature = document.getSignatures().get(0);
        assertEquals("LW", signature.getSignatory().getInitials());
        assertEquals(LocalDate.of(2018, 6, 5), signature.getSignedDate().get());
        assertEquals(UserRole.AUTHOR, signature.getSignatoryRole());
        assertTrue(signature.isConfirmed());
    }

    @Test
    public void shouldExtractWithSpecialName() {
        String content =
                "| Auteur : MYT (Non signé avant départ)| Vérificateur : Fleur Sibileau |\n" +
                "|Date: 02/05/2018|Date: 02/05/2018|\n" +
                "| Acceptation numérique : -- | Acceptation numérique : OK -- FS|";

        extractor.fillDocument(document, content);

        assertEquals(2, document.getSignatures().size());

        ApprovalSignature signature = document.getSignatures().get(0);
        assertEquals("MYT", signature.getSignatory().getInitials());
        assertEquals(ApprovalExtractor.WRONG_DATE, signature.getSignedDate().get());
        assertEquals(UserRole.AUTHOR, signature.getSignatoryRole());
        assertTrue(signature.isConfirmed());
    }
}