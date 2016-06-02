package uk.ac.ebi.solrReporter.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lucacherubin on 2016/06/02.
 */
public interface Report {

    boolean generateReport(ReportData data, File summary, File details);

    default FileWriter getAppender(File path) throws IOException {
        return new FileWriter(path,true);
    }


}
