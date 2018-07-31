package fr.axonic.avek.redmine.io.communication;

import fr.axonic.avek.redmine.models.UserIdentity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleIdentityBinder implements IdentityBinder {

    @Override
    public String getEmailAddress(UserIdentity identity) {
        /*if (identity.getInitials().equals("CD")) {
            return "cduffau@axonic.fr";
        } else if (identity.getInitials().equals("ME")) {
            return "meusebe@axonic.fr";
        }*/

        return "aaube@axonic.fr";
    }

    @Override
    public List<UserIdentity> getKnownUsers() {
        return Stream.of("AA", "CD", "ME", "JP", "MC", "LW", "FS", "JLD", "LG", "HC", "GS")
                .map(UserIdentity::new)
                .collect(Collectors.toList());
    }
}
