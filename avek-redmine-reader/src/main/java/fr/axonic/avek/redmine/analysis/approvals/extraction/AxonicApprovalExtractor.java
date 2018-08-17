package fr.axonic.avek.redmine.analysis.approvals.extraction;

import com.taskadapter.redmineapi.bean.WikiPage;
import com.taskadapter.redmineapi.bean.WikiPageDetail;
import fr.axonic.avek.redmine.analysis.approvals.ApprovalDocument;
import fr.axonic.avek.redmine.analysis.approvals.ApprovalSignature;
import fr.axonic.avek.redmine.users.UserIdentity;
import fr.axonic.avek.redmine.users.UserRole;
import fr.axonic.avek.redmine.users.bindings.IdentityBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;

public class AxonicApprovalExtractor extends ApprovalExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxonicApprovalExtractor.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uu");

    // TODO Remove this.
    private static final String[] IGNORED_FILES = {"SWAMMeeting060618", "SWAM_PDL_0001_A", "SWAM_PDL_0001_B",
            "SWAM_PDL_0001_C", "SWAM_PDL_0001_D", "SWAM_RDE_001_A", "SWAM_ST_0009_A", "SWAM_ST_0010_A",
            "SWAM_ST_0010_B", "SWAM_ST_0010_C", "SWAM_ST_0011_A", "SWAM_ST_0011_B", "SWAM_ST_0006_B",
            "SWAM_RFS_0006", "SWAM_RIN", "SWAM_FFC_0003_B"};

    public AxonicApprovalExtractor(IdentityBinder identityBinder) {
        super(identityBinder);
    }

    @Override
    public Optional<ApprovalDocument> extract(WikiPage wikiPage, WikiPageDetail pageDetail) {
        if (Arrays.asList(IGNORED_FILES).contains(wikiPage.getTitle())) {
            LOGGER.info("The page `{}` does not follow the structure.", wikiPage.getTitle());
            return Optional.of(new ApprovalDocument(wikiPage));
        }

        String context = pageDetail.getText();
        String[] splitText = context.toUpperCase().split("APPROBATION DU DOCUMENT");

        if (splitText.length == 1) {
            // No "validation" section.
            LOGGER.info("The page `{}` does not have an approbation section.", wikiPage.getTitle());
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(convertValidationText(wikiPage, splitText[1].trim()));
        } catch (Exception e) {
            LOGGER.info("The page `{}` does not follow the structure.", wikiPage.getTitle());
            LOGGER.error("Exception while extracting the validation document of `{}`.", wikiPage.getTitle(), e);

            return Optional.of(new ApprovalDocument(wikiPage));
        }
    }

    private ApprovalDocument convertValidationText(WikiPage page, String validationText) {
        String[] validationArrayLines = validationText.split("\n");

        String[] authors = parseArrayLine(validationArrayLines[0]);
        String[] dates = parseArrayLine(validationArrayLines[1]);
        String[] signatures = parseArrayLine(validationArrayLines[2]);

        ApprovalDocument document = new ApprovalDocument(page);

        for (int i = 0; i < authors.length; i++) {
            ApprovalSignature signature = new ApprovalSignature(null, null, null, false);

            try {
                signature = buildSignature(authors[i].trim(), dates[i].trim(), signatures[i].trim());
            } catch (Exception e) {
                LOGGER.error("Error while parsing a signature in page `{}`.", page.getTitle(), e);
            }

            document.getSignatures().add(signature);
        }

        return document;
    }

    private String[] parseArrayLine(String line) {
        line = line.trim();
        String[] split = line.substring(1, line.length() - 1).split("\\|");

        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }

        return split;
    }

    private ApprovalSignature buildSignature(String authorLine, String dateLine, String signatureLine) {
        String[] authorsSplit = authorLine.split(":");

        String authorIdField = authorsSplit.length > 1 ? authorsSplit[1].trim() : null;

        return new ApprovalSignature(
                parseIdentifier(authorIdField),
                parseRole(authorsSplit[0].trim()),
                parseDate(dateLine),
                !parseSignature(signatureLine).isEmpty());
    }

    private UserIdentity parseIdentifier(String identifierString) {
        if (identifierString == null) {
            return null;
        }

        identifierString = identifierString.replaceAll("\\(.*\\)", "").trim();
        identifierString = identifierString.replaceAll("\\.\\.+", "").trim();
        identifierString = identifierString.replaceAll("-", " ").replaceAll("\\.", ". ").trim();
        identifierString = identifierString.replaceAll(" +", " ");

        if (identifierString.length() < 4 && identifierString.equals(identifierString.toUpperCase())) {
            return getIdentityBinder().getUser(identifierString).orElse(null);
        }

        if (identifierString.isEmpty()) {
            return null;
        }

        String[] split = identifierString.split(" ");

        if (split.length == 0) {
            return getIdentityBinder().getUser(identifierString).orElse(null);
        }

        for (int i = 0; i < split.length; i++) {
            if (split[i].endsWith(".")) {
                split[i] = split[i].substring(0, split[i].length() - 1);
            } else {
                split[i] = split[i].substring(0, 1).toUpperCase();
            }
        }

        identifierString = String.join("", split);

        return getIdentityBinder().getUser(identifierString).orElse(null);
    }

    private UserRole parseRole(String roleString) {
        switch (roleString.trim()) {
            case "AUTEUR":
                return UserRole.AUTHOR;
            case "VÉRIFICATEUR":
                return UserRole.VERIFIER;
            default:
                return null;
        }
    }

    private LocalDate parseDate(String dateString) {
        String[] dateSplit = dateString.split(":");
        dateString = dateSplit.length > 1 ? dateSplit[1].trim() : "";

        if (dateString.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            LOGGER.error("`{}` is not a valid date", dateString, e);

            return WRONG_DATE;
        }
    }

    private String parseSignature(String signatureString) {
        String[] signatureSplit = signatureString.split(":");

        return signatureSplit.length > 1 ? signatureSplit[1].trim() : "";
    }
}
