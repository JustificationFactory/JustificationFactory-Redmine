package fr.axonic.avek.redmine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskadapter.redmineapi.RedmineException;
import fr.axonic.avek.redmine.analysis.WikiProjectProcessor;
import fr.axonic.avek.redmine.analysis.approvals.extraction.ApprovalExtractor;
import fr.axonic.avek.redmine.analysis.approvals.extraction.AxonicApprovalExtractor;
import fr.axonic.avek.redmine.analysis.notifications.NotificationSystem;
import fr.axonic.avek.redmine.analysis.notifications.NotificationSystemFactory;
import fr.axonic.avek.redmine.analysis.reporting.AnalysisFormatter;
import fr.axonic.avek.redmine.analysis.reporting.AnalysisReport;
import fr.axonic.avek.redmine.configuration.ConfigurationDocument;
import fr.axonic.avek.redmine.configuration.NotifierType;
import fr.axonic.avek.redmine.configuration.ProjectStatus;
import fr.axonic.avek.redmine.configuration.ProjectsDocument;
import fr.axonic.avek.redmine.transmission.RedmineSupportsTranslator;
import fr.axonic.avek.redmine.transmission.bus.AvekBusTransmitter;
import fr.axonic.avek.redmine.transmission.bus.ConcreteAvekBusTransmitter;
import fr.axonic.avek.redmine.transmission.bus.SilentAvekBusTransmitter;
import fr.axonic.avek.redmine.users.bindings.IdentityBinder;
import fr.axonic.avek.redmine.users.bindings.SimpleIdentityBinder;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AvekRedmineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvekRedmineRunner.class);

    private static final String CONFIGURATION_OPTION = "credentials";
    private static final String PROJECTS_OPTION = "projects";
    private static final String NOTIFIER_OPTION = "notifier";

    private static final String DEFAULT_CONFIGURATION = System.getProperty("user.home") + "/.avek/avek_redmine/configuration.json";
    private static final String DEFAULT_PROJECTS = System.getProperty("user.home") + "/.avek/avek_redmine/projects.json";
    private static final String DEFAULT_NOTIFIER = "SILENT";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) throws ParseException, IOException, RedmineException {
        CommandLine arguments = parseArguments(args);

        ConfigurationDocument configuration = MAPPER.readValue(new File(arguments.getOptionValue(CONFIGURATION_OPTION, DEFAULT_CONFIGURATION)), ConfigurationDocument.class);
        ProjectsDocument projects = parseProjects(new File(arguments.getOptionValue(PROJECTS_OPTION, DEFAULT_PROJECTS)), configuration);
        NotifierType notifierType = NotifierType.valueOf(arguments.getOptionValue(NOTIFIER_OPTION, DEFAULT_NOTIFIER));

        for (String projectName : configuration.getProjects()) {
            Optional<ProjectStatus> status = projects.getProject(projectName);

            if (status.isPresent()) {
                IdentityBinder identityBinder = new SimpleIdentityBinder();
                AvekBusTransmitter transmitter = new SilentAvekBusTransmitter(new RedmineSupportsTranslator(configuration, status.get()));//new ConcreteAvekBusTransmitter(new RedmineSupportsTranslator(configuration, status.get()), configuration);
                ApprovalExtractor extractor = new AxonicApprovalExtractor(identityBinder);
                NotificationSystem notifier = NotificationSystemFactory.getInstance().create(notifierType, LocalDateTime.MIN, configuration, status.get());

                AnalysisReport report = WikiProjectProcessor.builder(configuration)
                        .with(identityBinder)
                        .with(transmitter)
                        .with(extractor)
                        .with(notifier)
                        .forProject(status.get())
                        .runAnalysis();

                System.out.println(new AnalysisFormatter().formatHtml(report));

                // TODO Update the projects file.
            }
        }
    }

    private static CommandLine parseArguments(String[] args) throws ParseException {
        Options options = new Options();

        Option credentialsOption = new Option("c", CONFIGURATION_OPTION, true, "Path to the credentials file");
        credentialsOption.setRequired(false);
        options.addOption(credentialsOption);

        Option projectsOption = new Option("p", PROJECTS_OPTION, true, "Path to the projects file");
        projectsOption.setRequired(false);
        options.addOption(projectsOption);

        Option notifierOption = new Option("n", NOTIFIER_OPTION, true, "Type of notifier");
        notifierOption.setRequired(false);
        options.addOption(notifierOption);

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
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

            runConfiguration.getProjects().forEach(project -> projects.getProjects().add(new ProjectStatus(project, LocalDateTime.MIN)));

            if (projectsFile.getParentFile().mkdirs() && projectsFile.createNewFile()) {
                mapper.writeValue(projectsFile, projects);
            }

            return projects;
        }
    }
}
