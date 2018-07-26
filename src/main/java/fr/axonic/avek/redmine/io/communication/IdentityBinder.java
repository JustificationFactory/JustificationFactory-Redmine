package fr.axonic.avek.redmine.io.communication;

import fr.axonic.avek.redmine.models.UserIdentity;

public interface IdentityBinder {

    String getEmailAddress(UserIdentity identity);
}
