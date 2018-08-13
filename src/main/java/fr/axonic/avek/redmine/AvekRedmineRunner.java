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
import fr.axonic.avek.redmine.configuration.*;
import fr.axonic.avek.redmine.transmission.bus.AvekBusTransmitter;
import fr.axonic.avek.redmine.transmission.bus.AvekBusTransmitterFactory;
import fr.axonic.avek.redmine.users.bindings.IdentityBinder;
import fr.axonic.avek.redmine.users.bindings.SimpleIdentityBinder;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AvekRedmineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvekRedmineRunner.class);

    private static final String CONFIGURATION_OPTION = "credentials";
    private static final String PROJECTS_OPTION = "projects";
    private static final String NOTIFIER_OPTION = "notifier";
    private static final String DATE_OPTION = "date";
    private static final String TRANSMITTER_OPTION = "transmitter";

    private static final String DEFAULT_CONFIGURATION = System.getProperty("user.home") + "/.avek/avek_redmine/configuration.json";
    private static final String DEFAULT_PROJECTS = System.getProperty("user.home") + "/.avek/avek_redmine/projects.json";
    private static final String DEFAULT_NOTIFIER = "SILENT";
    private static final String DEFAULT_DATE = "2010-01-01 01:00:00";
    private static final String DEFAULT_TRANSMITTER = "SILENT";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        MAPPER.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) throws ParseException, IOException, RedmineException {
        CommandLine arguments = parseArguments(args);

        ConfigurationDocument configuration = MAPPER.readValue(new File(arguments.getOptionValue(CONFIGURATION_OPTION, DEFAULT_CONFIGURATION)), ConfigurationDocument.class);
        ProjectsDocument projects = parseProjects(new File(arguments.getOptionValue(PROJECTS_OPTION, DEFAULT_PROJECTS)), configuration);
        NotifierType notifierType = NotifierType.valueOf(arguments.getOptionValue(NOTIFIER_OPTION, DEFAULT_NOTIFIER));
        LocalDateTime minimumDate = LocalDateTime.parse(arguments.getOptionValue(DATE_OPTION, DEFAULT_DATE), DATE_FORMATTER);
        AvekBusTransmitterType transmitterType = AvekBusTransmitterType.valueOf(arguments.getOptionValue(TRANSMITTER_OPTION, DEFAULT_TRANSMITTER));

        for (String projectName : configuration.getProjects()) {
            Optional<ProjectStatus> status = projects.getProject(projectName);

            if (status.isPresent()) {
                IdentityBinder identityBinder = new SimpleIdentityBinder();
                ApprovalExtractor extractor = new AxonicApprovalExtractor(identityBinder);
                AvekBusTransmitter transmitter = AvekBusTransmitterFactory.getInstance().create(transmitterType, configuration, status.get());
                NotificationSystem notifier = NotificationSystemFactory.getInstance().create(notifierType, configuration, status.get());

                AnalysisReport report = WikiProjectProcessor.builder(configuration)
                        .with(identityBinder)
                        .with(transmitter)
                        .with(extractor)
                        .with(notifier)
                        .from(minimumDate)
                        .forProject(status.get())
                        .runAnalysis();

                saveReport(configuration, report);

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

        Option dateOption = new Option("d", DATE_OPTION, true, "Minimum date of verification");
        dateOption.setRequired(false);
        options.addOption(dateOption);


        Option transmitterOption = new Option("t", TRANSMITTER_OPTION, true, "Type of transmitter");
        transmitterOption.setRequired(false);
        options.addOption(transmitterOption);

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

    private static void saveReport(ConfigurationDocument configuration, AnalysisReport report) throws IOException {
        String reportAsHtml = new AnalysisFormatter().formatHtml(report);

        SmbFile remoteRankingFile = new SmbFile(configuration.getRemoteRankingFolder() + "/ranking.html");

        if (!remoteRankingFile.exists()) {
            remoteRankingFile.createNewFile();
        }

        try (SmbFileOutputStream remoteFileOutputStream = new SmbFileOutputStream(remoteRankingFile)) {
            remoteFileOutputStream.write(reportAsHtml.getBytes());
        }
    }
}
