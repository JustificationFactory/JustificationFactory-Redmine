package fr.axonic.jf.redmine.reader.users.bindings;

import fr.axonic.jf.redmine.reader.users.UserIdentity;

import java.util.*;

public class SimpleProjectIdentityBinder implements ProjectIdentityBinder {

    private static final Map<String, UserIdentity> USERS = new HashMap<>();

    static {
        USERS.put("AA", new UserIdentity("AA", "aaube@axonic.fr"));
        USERS.put("CD", new UserIdentity("CD", "cduffau@axonic.fr"));
        USERS.put("ME", new UserIdentity("ME", "meusebe@axonic.fr"));
        USERS.put("JP", new UserIdentity("JP", "jpradels@axonic.fr"));
        USERS.put("MC", new UserIdentity("MC", "mcombettes@axonic.fr"));
        USERS.put("FS", new UserIdentity("FS", "fsibileau@axonic.fr"));
        USERS.put("JLD", new UserIdentity("JLD", "jldivoux@axonic.fr"));
        USERS.put("LG", new UserIdentity("LG", "lguillemetz@axonic.fr"));
        USERS.put("HC", new UserIdentity("HC", "hcharou@axonic.fr"));
        USERS.put("GS", new UserIdentity("GS", "gsouquet@axonic.fr"));
    }

    @Override
    public UserIdentity getProjectManager() {
        return new UserIdentity("LG", "lguillemetz@axonic.fr");
    }

    @Override
    public Optional<UserIdentity> getUser(String initials) {
        return Optional.ofNullable(USERS.get(initials));
    }

    @Override
    public boolean knows(String initials) {
        return USERS.containsKey(initials);
    }

    @Override
    public List<UserIdentity> getKnownUsers() {
        return new ArrayList<>(USERS.values());
    }
}
