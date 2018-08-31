package fr.axonic.jf.redmine.masterfile.extractors;

import fr.axonic.jf.redmine.masterfile.MasterFile;

import java.io.IOException;

public interface MasterFileExtractor {

    MasterFile extract() throws IOException;
}
