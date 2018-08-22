package fr.axonic.avek.redmine.reader.users;

import java.util.Objects;

public class UserIdentity {

    private final String initials;
    private final String email;

    public UserIdentity(String initials, String email) {
        this.initials = initials;
        this.email = email;
    }

    public String getInitials() {
        return initials;
    }

    public String getEmail() {
        return email;
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
