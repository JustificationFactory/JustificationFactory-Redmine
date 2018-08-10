package fr.axonic.avek.redmine.users.bindings;

import fr.axonic.avek.redmine.users.UserIdentity;

import java.util.List;
import java.util.Optional;

public interface IdentityBinder {

    UserIdentity getDefaultUser();
    Optional<UserIdentity> getUser(String initials);
    List<UserIdentity> getKnownUsers();
}
