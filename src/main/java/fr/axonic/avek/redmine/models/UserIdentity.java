package fr.axonic.avek.redmine.models;

import java.util.Objects;

public class UserIdentity {

    private final String initials;

    public UserIdentity(String initials) {
        this.initials = initials;
    }

    public String getInitials() {
        return initials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserIdentity that = (UserIdentity) o;
        return Objects.equals(initials, that.initials);
    }

    @Override
    public int hashCode() {

        return Objects.hash(initials);
    }
}
