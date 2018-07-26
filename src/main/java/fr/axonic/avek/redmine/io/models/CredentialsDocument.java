package fr.axonic.avek.redmine.io.models;

public class CredentialsDocument {

    private String redmineUrl;
    private String redmineApiKey;
    private String emailAddress;
    private String emailPassword;
    private String emailHost;
    private String emailPort;

    public String getRedmineUrl() {
        return redmineUrl;
    }

    public void setRedmineUrl(String redmineUrl) {
        this.redmineUrl = redmineUrl;
    }

    public String getRedmineApiKey() {
        return redmineApiKey;
    }

    public void setRedmineApiKey(String redmineApiKey) {
        this.redmineApiKey = redmineApiKey;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(String emailPort) {
        this.emailPort = emailPort;
    }
}
