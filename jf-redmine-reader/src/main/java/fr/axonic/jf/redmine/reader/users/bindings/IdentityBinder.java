package fr.axonic.jf.redmine.reader.users.bindings;

import fr.axonic.jf.redmine.reader.users.UserIdentity;

import java.util.List;
import java.util.Optional;

public interface IdentityBinder {

    UserIdentity getDefaultUser();
    Optional<UserIdentity> getUser(String initials);
    List<UserIdentity> getKnownUsers();
}
