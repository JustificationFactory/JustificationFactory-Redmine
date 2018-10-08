package fr.axonic.jf.redmine.masterfile;

import fr.axonic.jf.redmine.masterfile.extractors.MasterFileExtractor;
import fr.axonic.jf.redmine.masterfile.formatters.MasterFileFormatter;

import java.io.IOException;

public class MasterFileGenerator {

    private MasterFileExtractor extractor;
    private MasterFileFormatter formatter;

    public MasterFileGenerator(MasterFileExtractor extractor, MasterFileFormatter formatter) {
        this.extractor = extractor;
        this.formatter = formatter;
    }

    public void execute() {
        try {
            MasterFile masterFile = extractor.extract();

            System.out.println(masterFile);

            System.out.println(masterFile.getDevelopmentDocuments());

            System.out.println(formatter.format(extractor.extract()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
