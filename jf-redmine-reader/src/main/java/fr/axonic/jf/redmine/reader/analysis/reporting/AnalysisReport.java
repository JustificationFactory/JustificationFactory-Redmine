package fr.axonic.jf.redmine.reader.analysis.reporting;

import fr.axonic.jf.redmine.reader.analysis.JustificationDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalDocument;
import fr.axonic.jf.redmine.reader.analysis.approvals.ApprovalSignature;
import fr.axonic.jf.redmine.reader.users.UserIdentity;

import java.util.*;

public class AnalysisReport {

    private Map<UserIdentity, UserPerformance> usersPerformances;
    private List<UserPerformance> performances;
    private int totalWikiPages;
    private int wikiPagesWithApproval;
    private int approvalsInError;

    private List<JustificationDocument> allJustificationDocuments;
    private List<JustificationDocument> validatedUnlockedJustificationDocuments;
    private List<JustificationDocument> unlockedJustificationDocumentsWithErrors;

    public AnalysisReport() {
        usersPerformances = new HashMap<>();
        performances = new ArrayList<>();
        totalWikiPages = 0;
        wikiPagesWithApproval = 0;
        approvalsInError = 0;

        allJustificationDocuments = new ArrayList<>();
        validatedUnlockedJustificationDocuments = new ArrayList<>();
        unlockedJustificationDocumentsWithErrors = new ArrayList<>();
    }

    public int getTotalWikiPages() {
        return totalWikiPages;
    }

    public int getWikiPagesWithApproval() {
        return wikiPagesWithApproval;
    }

    public int getApprovalsInError() {
        return approvalsInError;
    }

    public void setTotalWikiPages(int totalWikiPages) {
        this.totalWikiPages = totalWikiPages;
    }

    public void setWikiPagesWithApproval(int wikiPagesWithApproval) {
        this.wikiPagesWithApproval = wikiPagesWithApproval;
    }

    public void acknowledge(ApprovalDocument page, boolean isOk) {
        if (!isOk) {
            approvalsInError++;
        }

        for (ApprovalSignature signature : page.getSignatures()) {
            if (signature.getSignatory() != null) {
                getUser(signature.getSignatory()).acknowledge(page);
            }
        }

        updateOrder();
    }

    public List<UserPerformance> getPerformances() {
        return performances;
    }

    private void updateOrder() {
        performances.sort(Comparator.comparing(UserPerformance::getNumberOfTotalContributions));
    }

    private UserPerformance getUser(UserIdentity user) {
        if (!usersPerformances.containsKey(user)) {
            UserPerformance newPerformance = new UserPerformance(user);
            usersPerformances.put(user, newPerformance);
            performances.add(newPerformance);
        }

        return usersPerformances.get(user);
    }

    public static class UserPerformance {

        private final UserIdentity user;
        private int numberOfFaults;
        private int numberOfTotalContributions;

        public UserPerformance(UserIdentity user) {
            this.user = user;

            numberOfFaults = 0;
            numberOfTotalContributions = 0;
        }

        public void acknowledge(ApprovalDocument approval) {
            numberOfTotalContributions++;
        }

        public UserIdentity getUser() {
            return user;
        }

        public int getNumberOfFaults() {
            return numberOfFaults;
        }

        public int getNumberOfTotalContributions() {
            return numberOfTotalContributions;
        }
    }
}
