package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.sources.SourceFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AppStarter implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceFactory sourceFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Entering application...");

        Report report = new Report();
        report.setSamplesDB(sourceFactory.getDBSource().getSamplesAccessions());
        report.setGroupsDB(sourceFactory.getDBSource().getGroupsAccessions());
        report.setSamplesSolr(sourceFactory.getSolrSource().getSamplesAccessions());
        report.setGroupsSolr(sourceFactory.getSolrSource().getGroupsAccessions());
        report.setSamplesSolrMerged(sourceFactory.getSolrSource().getSamplesFromMergedCore());
        report.setGroupsSolrMerged(sourceFactory.getSolrSource().getGroupsFromMergedCore());

        System.out.println(report);
    }
}
