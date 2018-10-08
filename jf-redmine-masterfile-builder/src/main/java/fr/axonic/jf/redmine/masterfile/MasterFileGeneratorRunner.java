package fr.axonic.jf.redmine.masterfile;

import fr.axonic.jf.redmine.masterfile.extractors.MasterFileExtractor;
import fr.axonic.jf.redmine.masterfile.extractors.SimpleMasterFileExtractor;
import fr.axonic.jf.redmine.masterfile.formatters.MarkdownMasterFileFormatter;
import fr.axonic.jf.redmine.masterfile.formatters.MasterFileFormatter;
import org.apache.commons.cli.*;

public class MasterFileGeneratorRunner {

    public static void main(String[] args) throws ParseException {
        CommandLine arguments = parseArguments(args);

        String wsUrl = arguments.getOptionValue("ws");

        MasterFileExtractor extractor = new SimpleMasterFileExtractor(wsUrl, "SWAM");
        MasterFileFormatter formatter = new MarkdownMasterFileFormatter();

        MasterFileGenerator generator = new MasterFileGenerator(extractor, formatter);
        generator.execute();
    }

    private static CommandLine parseArguments(String[] args) throws ParseException {
        Options options = new Options();

        Option wsUrlOption = new Option("w", "ws", true, "URL of the webservice");
        wsUrlOption.setRequired(true);
        options.addOption(wsUrlOption);

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
    }
}
