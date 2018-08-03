package fr.axonic.avek.redmine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import fr.axonic.avek.redmine.io.communication.IdentityBinder;
import fr.axonic.avek.redmine.io.communication.MailSender;
import fr.axonic.avek.redmine.io.communication.SimpleIdentityBinder;
import fr.axonic.avek.redmine.io.models.ConfigurationDocument;
import fr.axonic.avek.redmine.io.models.ProjectsDocument;
import fr.axonic.avek.redmine.processes.ProjectWikiProcessor;
import fr.axonic.avek.redmine.processes.implementations.MailVerifiersNotifier;
import fr.axonic.avek.redmine.processes.implementations.SimpleValidationExtractor;
import fr.axonic.avek.redmine.processes.ranking.RankingWikiGenerator;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);
    private static final String CONFIGURATION_OPTION = "credentials";
    private static final String PROJECTS_OPTION = "projects";
    private static final String DEFAULT_CONFIGURATION = System.getProperty("user.home") + "/.avek/avek_redmine/configuration.json";
    private static final String DEFAULT_PROJECTS = System.getProperty("user.home") + "/.avek/avek_redmine/projects.json";

    public static void main(String[] args) throws ParseException, IOException, RedmineException {
        CommandLine arguments = parseArguments(args);

        ConfigurationDocument configuration = parseCredentials(new File(arguments.getOptionValue(CONFIGURATION_OPTION, DEFAULT_CONFIGURATION)));
        ProjectsDocument projects = parseProjects(new File(arguments.getOptionValue(PROJECTS_OPTION, DEFAULT_PROJECTS)), configuration);

        RedmineManager redmineManager = RedmineManagerFactory.createWithApiKey(configuration.getRedmineUrl(), configuration.getRedmineApiKey());
        IdentityBinder identityBinder = new SimpleIdentityBinder();
        MailSender sender = new MailSender(configuration, identityBinder);

        MailVerifiersNotifier notifier = new MailVerifiersNotifier(identityBinder, configuration.getRedmineUrl(), sender);

        ProjectWikiProcessor processor = new ProjectWikiProcessor(redmineManager, new SimpleValidationExtractor(), notifier);

        for (ProjectsDocument.ProjectStatus status : projects.getProjects()) {
            notifier.setCurrentProject(status.getProjectName());
            processor.processWiki(status);

            System.out.println(new RankingWikiGenerator().generateMarkdown(RankingSingleton.rankingData));
        }

        // TODO Update projects file.
    }

    private static CommandLine parseArguments(String[] args) throws ParseException {
        Options options = new Options();

        Option credentialsOption = new Option("c", CONFIGURATION_OPTION, true, "Path to the credentials file");
        credentialsOption.setRequired(false);
        options.addOption(credentialsOption);

        Option projectsOption = new Option("p", PROJECTS_OPTION, true, "Path to the projects file");
        projectsOption.setRequired(false);
        options.addOption(projectsOption);

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
    }

    private static ConfigurationDocument parseCredentials(File credentialsFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ConfigurationDocument credentials = mapper.readValue(credentialsFile, ConfigurationDocument.class);
        LOGGER.info("Credentials read from `{}`.", credentialsFile.getAbsolutePath());

        return credentials;
    }

    private static ProjectsDocument parseProjects(File projectsFile, ConfigurationDocument runConfiguration) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        if (projectsFile.exists()) {
            ProjectsDocument projects = mapper.readValue(projectsFile, ProjectsDocument.class);
            LOGGER.info("Projects read from `{}`.", projectsFile.getAbsolutePath());
            return projects;
        } else {
            ProjectsDocument projects = new ProjectsDocument();

            runConfiguration.getProjects().forEach(project -> projects.getProjects().add(new ProjectsDocument.ProjectStatus(project, LocalDateTime.MIN)));

            if (projectsFile.getParentFile().mkdirs() && projectsFile.createNewFile()) {
                mapper.writeValue(projectsFile, projects);
            }

            return projects;
        }
    }
}
