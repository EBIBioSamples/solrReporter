package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.report.Report;
import uk.ac.ebi.solrReporter.report.ReportData;
import uk.ac.ebi.solrReporter.report.XMLReport;
import uk.ac.ebi.solrReporter.report.XMLReportData;
import uk.ac.ebi.solrReporter.sources.SourceFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AppStarter implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceFactory sourceFactory;

    @Autowired
    private Report report;

    @Autowired
    private XMLReport xmlReport;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting Report");

        ReportData data = new ReportData();
        data.setSamplesDB(sourceFactory.getDBSource().getSamplesAccessions());
        data.setGroupsDB(sourceFactory.getDBSource().getGroupsAccessions());
//        data.setSamplesSolr(sourceFactory.getSolrSource().getSamplesAccessions());
//        data.setGroupsSolr(sourceFactory.getSolrSource().getGroupsAccessions());
//        data.setSamplesSolrMerged(sourceFactory.getSolrSource().getSamplesFromMergedCore());
//        data.setGroupsSolrMerged(sourceFactory.getSolrSource().getGroupsFromMergedCore());

        // XML part


//        queryStrings.addAll(Arrays.asList("EBiSC","HipSci","Plant","FAANG"));
//        xmlData.setQueryStrings(queryStrings);

//        log.info(data.toString());
        log.info(data.toString());

//        report.generateReport(data);
        xmlReport.generateReport(data);
    }
}
