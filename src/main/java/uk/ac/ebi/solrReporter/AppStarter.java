package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.sources.DBSourceImp;
import uk.ac.ebi.solrReporter.sources.SolrSource;

import java.util.List;

@Component
public class AppStarter implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DBSourceImp dbSourceImp;

    @Autowired
    private SolrSource solrSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Entering application...");

        List<String> list1 = dbSourceImp.getSamplesAccessions();
        log.info("Found " + list1.size() + " samples!!!");

        List<String> list2 = dbSourceImp.getGroupsAccessions();
        log.info("Found " + list2.size() + " groups!!!");

        solrSource.getSamplesAccessions();

        solrSource.getGroupsAccessions();
    }
}
