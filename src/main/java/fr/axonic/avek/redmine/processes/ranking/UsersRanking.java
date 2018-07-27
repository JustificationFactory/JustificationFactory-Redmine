package fr.axonic.avek.redmine.processes.ranking;

import fr.axonic.avek.redmine.io.communication.IdentityBinder;
import fr.axonic.avek.redmine.models.UserIdentity;
import fr.axonic.avek.redmine.processes.notifications.NotificationTopic;
import fr.axonic.avek.redmine.processes.notifications.VerifiersNotification;

import java.util.*;
import java.util.stream.Collectors;

public class UsersRanking {

    private Map<UserIdentity, UserContribution> usersContributions;

    public UsersRanking() {
        usersContributions = new HashMap<>();
    }

    public List<UserContribution> getOrderedUsersWithMostFailures() {
        return usersContributions.values().stream()
                .sorted(Comparator.comparing(UserContribution::getNumberOfValidationFailures))
                .collect(Collectors.toList());
    }

    public List<UserContribution> getOrderedUsersWithLessFailures() {
        return usersContributions.values().stream()
                .sorted(Comparator.comparing(UserContribution::getNumberOfValidationFailures).reversed())
                .collect(Collectors.toList());
    }

    public static class UserContribution {
        private UserIdentity user;
        private int numberOfUnsigned;
        private int numberOfWrongDates;
        private int numberOfValidationFailures;
        private int numberOfWellSigned;

        public UserIdentity getUser() {
            return user;
        }

        public void setUser(UserIdentity user) {
            this.user = user;
        }

        public int getNumberOfUnsigned() {
            return numberOfUnsigned;
        }

        public void setNumberOfUnsigned(int numberOfUnsigned) {
            this.numberOfUnsigned = numberOfUnsigned;
        }

        public int getNumberOfWrongDates() {
            return numberOfWrongDates;
        }

        public void setNumberOfWrongDates(int numberOfWrongDates) {
            this.numberOfWrongDates = numberOfWrongDates;
        }

        public void setNumberOfValidationFailures(int numberOfValidationFailures) {
            this.numberOfValidationFailures = numberOfValidationFailures;
        }

        public int getNumberOfValidationFailures() {
            return numberOfValidationFailures;
        }

        public int getNumberOfWellSigned() {
            return numberOfWellSigned;
        }

        public void setNumberOfWellSigned(int numberOfWellSigned) {
            this.numberOfWellSigned = numberOfWellSigned;
        }
    }

    public static class Builder {

        private Map<UserIdentity, List<VerifiersNotification>> notifications;

        public Builder(IdentityBinder identityBinder) {
            notifications = new HashMap<>();
            identityBinder.getKnownUsers().forEach(user -> notifications.put(user, new ArrayList<>()));
        }

        public Builder withNotifications(Map<UserIdentity, List<VerifiersNotification>> notifications) {
            notifications.forEach((user, userNotifications) -> this.notifications.put(user, userNotifications));

            return this;
        }

        public UsersRanking build() {
            UsersRanking ranking = new UsersRanking();

            notifications.forEach((user, userNotifications) -> {
                UserContribution contribution = new UserContribution();
                contribution.user = user;
                contribution.numberOfValidationFailures = userNotifications.size();
                contribution.numberOfUnsigned = (int) userNotifications.stream().filter(n -> n.getTopic() == NotificationTopic.NOT_SIGNED).count();
                contribution.numberOfWrongDates = (int) userNotifications.stream().filter(n -> n.getTopic() == NotificationTopic.MISSING_DATE).count();
                contribution.numberOfWellSigned = (int) userNotifications.stream().filter(n -> n.getTopic() == NotificationTopic.OK).count();

                ranking.usersContributions.put(user, contribution);
            });

            return ranking;
        }
    }
}
