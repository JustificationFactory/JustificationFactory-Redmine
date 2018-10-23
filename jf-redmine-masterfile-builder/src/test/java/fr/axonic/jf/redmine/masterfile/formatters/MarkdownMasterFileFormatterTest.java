package fr.axonic.jf.redmine.masterfile.formatters;

import fr.axonic.jf.instance.redmine.RedmineDocument;
import fr.axonic.jf.redmine.masterfile.MasterFile;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class MarkdownMasterFileFormatterTest {

    private MarkdownMasterFileFormatter formatter;

    @Before
    public void initialize() {
        formatter = new MarkdownMasterFileFormatter();
    }

    @Test
    public void shouldTranslateEmptyMasterFile() {
        MasterFile masterFile = new MasterFile("SWAM");

        String expected = "|{background:lightblue}.*Phase projet*|{background:lightblue}.*Type*|{background:lightblue}.*Nom*|{background:lightblue}.*Version*|{background:lightblue}.*Lien*|{background:lightblue}.*Auteur*|{background:lightblue}.*Date*|" + System.lineSeparator() +
                "|{background:lightgrey}.*INITIALISATION*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|{background:lightgrey}.*DONNÉES D'ENTRÉE*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|{background:lightgrey}.*FAISABILITÉ*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|{background:lightgrey}.*DÉVELOPPEMENT*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator();

        assertEquals(expected, formatter.format(masterFile));
    }

    @Test
    public void shouldTranslateOneEntryDataDocument() {
        RedmineDocument document = new RedmineDocument("http://a-url.com");
        document.setDocumentType("EE");
        document.setReference("SWAM_EE_001_A");
        document.setName("SWAM_EE_001");
        document.setAuthor("LG");
        document.setReleaseDate(LocalDate.of(2017, 7, 4));
        document.setVersion("A");

        MasterFile masterFile = new MasterFile("SWAM");
        masterFile.getEntryDataDocuments().add(document);

        String expected = "|{background:lightblue}.*Phase projet*|{background:lightblue}.*Type*|{background:lightblue}.*Nom*|{background:lightblue}.*Version*|{background:lightblue}.*Lien*|{background:lightblue}.*Auteur*|{background:lightblue}.*Date*|" + System.lineSeparator() +
                "|{background:lightgrey}.*INITIALISATION*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|{background:lightgrey}.*DONNÉES D'ENTRÉE*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|Données d'entrée|EE|SWAM_EE_001|A|[[SWAM:SWAM_EE_001_A]]|LG|04/07/17|" + System.lineSeparator() +
                "|{background:lightgrey}.*FAISABILITÉ*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|{background:lightgrey}.*DÉVELOPPEMENT*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator();

        assertEquals(expected, formatter.format(masterFile));
    }

    @Test
    public void shouldTranslateTwoEntryDataDocument() {
        RedmineDocument docA = new RedmineDocument("http://a-url.com");
        docA.setDocumentType("EE");
        docA.setReference("SWAM_EE_001_A");
        docA.setName("SWAM_EE_001");
        docA.setAuthor("LG");
        docA.setReleaseDate(LocalDate.of(2017, 7, 4));
        docA.setVersion("A");

        RedmineDocument docB = new RedmineDocument("http://a-url.com");
        docB.setDocumentType("EE");
        docB.setReference("SWAM_EE_001_B");
        docB.setName("SWAM_EE_001");
        docB.setAuthor("LG");
        docB.setReleaseDate(LocalDate.of(2017, 7, 4));
        docB.setVersion("B");

        MasterFile masterFile = new MasterFile("SWAM");
        masterFile.getEntryDataDocuments().add(docA);
        masterFile.getEntryDataDocuments().add(docB);

        String expected = "|{background:lightblue}.*Phase projet*|{background:lightblue}.*Type*|{background:lightblue}.*Nom*|{background:lightblue}.*Version*|{background:lightblue}.*Lien*|{background:lightblue}.*Auteur*|{background:lightblue}.*Date*|" + System.lineSeparator() +
                "|{background:lightgrey}.*INITIALISATION*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|{background:lightgrey}.*DONNÉES D'ENTRÉE*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|Données d'entrée|EE|SWAM_EE_001|A|[[SWAM:SWAM_EE_001_A]]|LG|04/07/17|" + System.lineSeparator() +
                "|Données d'entrée|EE|SWAM_EE_001|B|[[SWAM:SWAM_EE_001_B]]|LG|04/07/17|" + System.lineSeparator() +
                "|{background:lightgrey}.*FAISABILITÉ*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator() +
                "|{background:lightgrey}.*DÉVELOPPEMENT*|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|{background:lightgrey}.|" + System.lineSeparator();

        assertEquals(expected, formatter.format(masterFile));
    }
}