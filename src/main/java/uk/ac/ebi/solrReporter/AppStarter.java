package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.report.ReportData;
import uk.ac.ebi.solrReporter.sources.SourceFactory;

@Component
public class AppStarter implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceFactory sourceFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting Report");

        ReportData reportData = new ReportData();
        reportData.setSamplesDB(sourceFactory.getDBSource().getSamplesAccessions());
        reportData.setGroupsDB(sourceFactory.getDBSource().getGroupsAccessions());
        reportData.setSamplesSolr(sourceFactory.getSolrSource().getSamplesAccessions());
        reportData.setGroupsSolr(sourceFactory.getSolrSource().getGroupsAccessions());
        reportData.setSamplesSolrMerged(sourceFactory.getSolrSource().getSamplesFromMergedCore());
        reportData.setGroupsSolrMerged(sourceFactory.getSolrSource().getGroupsFromMergedCore());

        System.out.println(reportData);
    }
}
