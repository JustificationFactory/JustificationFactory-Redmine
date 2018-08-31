package fr.axonic.jf.redmine.masterfile;

import fr.axonic.jf.redmine.masterfile.extractors.MasterFileExtractor;
import fr.axonic.jf.redmine.masterfile.formatters.MasterFileFormatter;

public class MasterFileGenerator {

    private MasterFileExtractor extractor;
    private MasterFileFormatter formatter;

    public MasterFileGenerator(MasterFileExtractor extractor, MasterFileFormatter formatter) {
        this.extractor = extractor;
        this.formatter = formatter;
    }

    public void execute() {
        // TODO
    }
}
