package fr.axonic.avek.redmine.io.communication;

import fr.axonic.avek.redmine.models.UserIdentity;

public class SimpleIdentityBinder implements IdentityBinder {

    @Override
    public String getEmailAddress(UserIdentity identity) {
        if (identity.getInitials().equals("CD")) {
            return "cduffau@axonic.fr";
        }

        return "aaube@axonic.fr";
    }
}
