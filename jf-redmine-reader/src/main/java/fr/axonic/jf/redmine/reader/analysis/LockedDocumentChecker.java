package fr.axonic.jf.redmine.reader.analysis;

import com.taskadapter.redmineapi.bean.WikiPage;
import fr.axonic.jf.redmine.reader.configuration.RedmineDatabaseCredentials;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LockedDocumentChecker {

    private static final String IS_LOCKED_QUERY = "SELECT `title`, `protected` FROM `wiki_pages`;";

    private final RedmineDatabaseCredentials databaseCredentials;
    private final Map<String, Boolean> documentsStatus;

    public LockedDocumentChecker(RedmineDatabaseCredentials databaseCredentials) {
        this.databaseCredentials = databaseCredentials;

        documentsStatus = new HashMap<>();
    }

    public boolean isLocked(WikiPage page) {
        if (!documentsStatus.containsKey(page.getTitle())) {
            updateDocumentsStatus();
        }

        return documentsStatus.get(page.getTitle());
    }

    private void updateDocumentsStatus() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(databaseCredentials.getUrl(), databaseCredentials.getUser(), databaseCredentials.getPassword());
                 Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery(IS_LOCKED_QUERY)) {

                while (results.next()) {
                    documentsStatus.put(results.getString("title"), results.getBoolean("protected"));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
