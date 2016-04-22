package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.sources.SourceFactory;

import java.util.List;

@Component
public class AppStarter implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceFactory sourceFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Entering application...");

        List<String> list1 = sourceFactory.getDBSource().getSamplesAccessions();
        log.info("Found " + list1.size() + " samples!!!");

        List<String> list2 = sourceFactory.getDBSource().getGroupsAccessions();
        log.info("Found " + list2.size() + " groups!!!");

        sourceFactory.getSolrSource().getSamplesAccessions();

        sourceFactory.getSolrSource().getGroupsAccessions();
    }
}
