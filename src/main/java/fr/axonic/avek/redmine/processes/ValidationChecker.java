package fr.axonic.avek.redmine.processes;

import fr.axonic.avek.redmine.models.UserRole;
import fr.axonic.avek.redmine.models.ValidationDocument;
import fr.axonic.avek.redmine.models.ValidationSignature;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ValidationChecker {

    private final VerifiersNotifier notifier;

    public ValidationChecker(VerifiersNotifier notifier) {
        this.notifier = notifier;
    }

    public boolean verify(ValidationDocument document) {
        if (!hasAuthors(document) || !authorsConfirmed(document)) {
            // The authors have not locked the document.
            return false;
        }

        return document.getSignatures().stream()
                .map(s -> verifySignature(document, s))
                .reduce(Boolean::logicalAnd).orElse(true);
    }

    private boolean hasAuthors(ValidationDocument document) {
        List<ValidationSignature> authorsSignatures = document.getSignatures().stream()
                .filter(s -> s.getSignatoryRole() == UserRole.AUTHOR)
                .collect(Collectors.toList());


        if (authorsSignatures.isEmpty() || authorsSignatures.stream().anyMatch(s -> s.getSignatory() == null)) {
            notifier.noAuthor(document.getWikiPage());
            return false;
        }

        return true;
    }

    private boolean authorsConfirmed(ValidationDocument document) {
        return document.getSignatures().stream()
                .filter(s -> s.getSignatoryRole() == UserRole.AUTHOR)
                .allMatch(ValidationSignature::isConfirmed);
    }

    private boolean verifySignature(ValidationDocument document, ValidationSignature signature) {
        if (!signature.isConfirmed()) {
            notifier.notSigned(signature.getSignatory(), document.getWikiPage());

            return false;
        }

        boolean ok = true;

        if (!signature.getSignedDate().isPresent()) {
            notifier.missingDate(signature.getSignatory(), document.getWikiPage());
            ok = false;
        }

        return ok;
    }

    private boolean verifiersConfirmed(ValidationDocument document) {
        for (ValidationSignature signature : document.getSignatures()) {
            if (!signature.isConfirmed()) {
                notifier.notSigned(signature.getSignatory(), document.getWikiPage());
            }
        }

        return document.getSignatures().stream().allMatch(ValidationSignature::isConfirmed);
    }

    private boolean verifiersHavePutAGoodDate(ValidationDocument document) {
        LocalDate latestAuthorSignatureDate = document.getSignatures().stream()
                .filter(s -> s.getSignatoryRole() == UserRole.AUTHOR)
                .map(ValidationSignature::getSignedDate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce((a, b) -> a.isBefore(b) ? b : a)
                .orElse(LocalDate.now());

        List<ValidationSignature> verifiersSignatures = document.getSignatures().stream()
                .filter(s -> s.getSignatoryRole() == UserRole.VERIFIER)
                .filter(ValidationSignature::isValidated)
                .collect(Collectors.toList());
        for (ValidationSignature verifierSignature : verifiersSignatures) {
            Optional<LocalDate> optionalSignedDate = verifierSignature.getSignedDate();

            if (optionalSignedDate.isPresent()) {
                LocalDate signedDate = optionalSignedDate.get();

                if (signedDate.isBefore(latestAuthorSignatureDate)) {
                    notifier.signedBeforeAuthorValidation(verifierSignature.getSignatory(), document.getWikiPage());
                    return false;
                }
            } else {
                notifier.missingDate(verifierSignature.getSignatory(), document.getWikiPage());
                return false;
            }
        }

        return true;
    }
}
