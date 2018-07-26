package fr.axonic.avek.redmine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import fr.axonic.avek.redmine.io.communication.MailSender;
import fr.axonic.avek.redmine.io.communication.SimpleIdentityBinder;
import fr.axonic.avek.redmine.io.models.CredentialsDocument;
import fr.axonic.avek.redmine.io.models.ProjectsDocument;
import fr.axonic.avek.redmine.processes.ProjectWikiProcessor;
import fr.axonic.avek.redmine.processes.implementations.MailVerifiersNotifier;
import fr.axonic.avek.redmine.processes.implementations.SimpleValidationExtractor;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Runner {

    private static final String CREDENTIALS_OPTION = "credentials";
    private static final String PROJECTS_OPTION = "projects";
    private static final String DEFAULT_CREDENTIALS = System.getProperty("user.home") + "/.avek/avek_redmine/credentials.json";
    private static final String DEFAULT_PROJECTS = System.getProperty("user.home") + "/.avek/avek_redmine/projects.json";
    private static final Scanner STANDARD_INPUT = new Scanner(System.in);

    public static void main(String[] args) throws ParseException, IOException, RedmineException {
        CommandLine arguments = parseArguments(args);

        CredentialsDocument credentialsDocument = parseCredentials(new File(arguments.getOptionValue(CREDENTIALS_OPTION, DEFAULT_CREDENTIALS)));
        ProjectsDocument projectsDocument = parseProjects(new File(arguments.getOptionValue(PROJECTS_OPTION, DEFAULT_PROJECTS)));

        RedmineManager redmineManager = RedmineManagerFactory.createWithApiKey(credentialsDocument.getRedmineUrl(), credentialsDocument.getRedmineApiKey());
        MailSender sender = new MailSender(credentialsDocument, new SimpleIdentityBinder());

        MailVerifiersNotifier notifier = new MailVerifiersNotifier(credentialsDocument.getRedmineUrl(), sender);

        ProjectWikiProcessor processor = new ProjectWikiProcessor(redmineManager, new SimpleValidationExtractor(), notifier);

        for (ProjectsDocument.ProjectStatus status : projectsDocument.getProjects()) {
            notifier.setCurrentProject(status.getProjectName());
            processor.processWiki(status);
        }
    }

    private static CommandLine parseArguments(String[] args) throws ParseException {
        Options options = new Options();

        Option credentialsOption = new Option("c", CREDENTIALS_OPTION, true, "Path to the credentials file");
        credentialsOption.setRequired(false);
        options.addOption(credentialsOption);

        Option projectsOption = new Option("p", PROJECTS_OPTION, true, "Path to the projects file");
        projectsOption.setRequired(false);
        options.addOption(projectsOption);

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
    }

    private static CredentialsDocument parseCredentials(File credentialsFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        if (credentialsFile.exists()) {
            CredentialsDocument credentials = mapper.readValue(credentialsFile, CredentialsDocument.class);
            show("Credentials read from `" + credentialsFile.getAbsolutePath() + "`.\n");
            return credentials;
        } else {
            CredentialsDocument credentials = new CredentialsDocument();

            show("Initialize credentials file.");
            prompt("Redmine URL");
            credentials.setRedmineUrl(STANDARD_INPUT.next());
            prompt("Redmine API key");
            credentials.setRedmineApiKey(STANDARD_INPUT.next());
            prompt("Email address");
            credentials.setEmailAddress(STANDARD_INPUT.next());
            prompt("Email password");
            credentials.setEmailPassword(STANDARD_INPUT.next());
            prompt("Email host");
            credentials.setEmailHost(STANDARD_INPUT.next());
            prompt("Email port");
            credentials.setEmailPort(STANDARD_INPUT.next());

            credentialsFile.getParentFile().mkdirs();
            credentialsFile.createNewFile();
            mapper.writeValue(credentialsFile, credentials);

            show("Credentials are saved in `" + credentialsFile.getAbsolutePath() + "`.\n");

            return credentials;
        }
    }

    private static ProjectsDocument parseProjects(File projectsFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        if (projectsFile.exists()) {
            ProjectsDocument projects = mapper.readValue(projectsFile, ProjectsDocument.class);
            show("Projects read from `" + projectsFile.getAbsolutePath() + "`.\n");
            return projects;
        } else {
            ProjectsDocument projects = new ProjectsDocument();

            show("Initialize projects file.");
            prompt("Project name");
            projects.getProjects().add(new ProjectsDocument.ProjectStatus(STANDARD_INPUT.next(), LocalDateTime.MIN));

            projectsFile.getParentFile().mkdirs();
            projectsFile.createNewFile();
            mapper.writeValue(projectsFile, projects);

            show("Projects are saved in `" + projectsFile.getAbsolutePath() + "`.\n");

            return projects;
        }
    }

    private static void show(String content) {
        System.out.println("# " + content);
    }

    private static void prompt(String content) {
        System.out.print(content + ": ");
    }
}
