package fr.axonic.jf.redmine.reader.analysis.approvals;

import fr.axonic.jf.redmine.reader.users.UserIdentity;
import fr.axonic.jf.redmine.reader.users.UserRole;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ApprovalSignature {

    private final UserIdentity signatory;
    private final UserRole signatoryRole;
    private final LocalDate signedDate;
    private final boolean confirmed;

    public ApprovalSignature(UserIdentity signatory, UserRole signatoryRole, LocalDate signedDate, boolean confirmed) {
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

    @Override
    public String toString() {
        String signatoryInitials = signatory == null ? "?" : signatory.getInitials();
        String formattedDate = signedDate == null ? "?" : signedDate.format(DateTimeFormatter.ISO_DATE);

        return signatoryInitials + " (" + signatoryRole + "), " + formattedDate + ": " + confirmed;
    }
}
