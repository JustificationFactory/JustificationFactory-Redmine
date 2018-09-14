package fr.axonic.jf.redmine.reader.users.bindings;

import fr.axonic.jf.redmine.reader.users.UserIdentity;

import java.util.List;
import java.util.Optional;

public interface ProjectIdentityBinder {

    UserIdentity getProjectManager();
    Optional<UserIdentity> getUser(String initials);
    boolean knows(String initials);
    List<UserIdentity> getKnownUsers();
}
