package fr.axonic.avek.redmine.io.communication;

import fr.axonic.avek.redmine.models.UserIdentity;

import java.util.List;

public interface IdentityBinder {

    String getEmailAddress(UserIdentity identity);
    List<UserIdentity> getKnownUsers();
}
