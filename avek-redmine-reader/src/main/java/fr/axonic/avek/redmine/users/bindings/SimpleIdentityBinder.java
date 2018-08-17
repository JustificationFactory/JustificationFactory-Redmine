package fr.axonic.avek.redmine.users.bindings;

import fr.axonic.avek.redmine.users.UserIdentity;

import java.util.*;

public class SimpleIdentityBinder implements IdentityBinder {

    private static final Map<String, UserIdentity> USERS = new HashMap<>();

    static {
        USERS.put("AA", new UserIdentity("AA", "aaube@axonic.fr"));
        USERS.put("CD", new UserIdentity("CD", "cduffau@axonic.fr"));
        USERS.put("ME", new UserIdentity("ME", "meusebe@axonic.fr"));
        USERS.put("JP", new UserIdentity("JP", "jpradels@axonic.fr"));
        USERS.put("MC", new UserIdentity("MC", "mcombettes@axonic.fr"));
        USERS.put("LW", new UserIdentity("LW", "lwauters@axonic.fr"));
        USERS.put("FS", new UserIdentity("FS", "fsibileau@axonic.fr"));
        USERS.put("JLD", new UserIdentity("JLD", "jldivoux@axonic.fr"));
        USERS.put("LG", new UserIdentity("LG", "lguillemetz@axonic.fr"));
        USERS.put("HC", new UserIdentity("HC", "hcharou@axonic.fr"));
        USERS.put("GS", new UserIdentity("GS", "gsouquet@axonic.fr"));
    }

    @Override
    public UserIdentity getDefaultUser() {
        return new UserIdentity("Project Owner", "lguillemetz@axonic.fr");
    }

    @Override
    public Optional<UserIdentity> getUser(String initials) {
        return Optional.ofNullable(USERS.get(initials));
    }

    @Override
    public List<UserIdentity> getKnownUsers() {
        return new ArrayList<>(USERS.values());
    }
}
