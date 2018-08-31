package fr.axonic.jf.redmine.masterfile;

import fr.axonic.jf.redmine.masterfile.extractors.MasterFileExtractor;
import fr.axonic.jf.redmine.masterfile.extractors.SimpleMasterFileExtractor;
import fr.axonic.jf.redmine.masterfile.formatters.MarkdownMasterFileFormatter;
import fr.axonic.jf.redmine.masterfile.formatters.MasterFileFormatter;
import org.apache.commons.cli.*;

public class MasterFileGeneratorRunner {

    public static void main(String[] args) throws ParseException {
        CommandLine arguments = parseArguments(args);

        String busUrl = arguments.getOptionValue("bus");

        MasterFileExtractor extractor = new SimpleMasterFileExtractor(busUrl, "SWAM");
        MasterFileFormatter formatter = new MarkdownMasterFileFormatter();

        MasterFileGenerator generator = new MasterFileGenerator(extractor, formatter);
        generator.execute();
    }

    private static CommandLine parseArguments(String[] args) throws ParseException {
        Options options = new Options();

        Option busUrlOption = new Option("b", "bus", true, "URL of the bus");
        busUrlOption.setRequired(true);
        options.addOption(busUrlOption);

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
    }
}
