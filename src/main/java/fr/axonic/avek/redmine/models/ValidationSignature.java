package fr.axonic.avek.redmine.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ValidationSignature {

    private final UserIdentity signatory;
    private final UserRole signatoryRole;
    private final LocalDate signedDate;
    private final boolean confirmed;

    public ValidationSignature(UserIdentity signatory, UserRole signatoryRole, LocalDate signedDate, boolean confirmed) {
        this.signatory = signatory;
        this.signatoryRole = signatoryRole;
        this.signedDate = signedDate;
        this.confirmed = confirmed;
    }

    public UserIdentity getSignatory() {
        return signatory;
    }

    public UserRole getSignatoryRole() {
        return signatoryRole;
    }

    public Optional<LocalDate> getSignedDate() {
        return Optional.ofNullable(signedDate);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isValidated() {
        return confirmed && signedDate != null && signatoryRole != null && signatory != null;
    }

    @Override
    public String toString() {
        String signatoryInitials = signatory == null ? "?" : signatory.getInitials();
        String formattedDate = signedDate == null ? "?" : signedDate.format(DateTimeFormatter.ISO_DATE);

        return signatoryInitials + " (" + signatoryRole + "), " + formattedDate + ": " + confirmed;
    }
}
