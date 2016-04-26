package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.sources.SourceFactory;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Generates list of accessions missing in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public void getMissingInIndex(List<String> dbAcc, List<String> solrAcc) {
        List<String> missinInIndex = dbAcc.stream().filter(db -> !solrAcc.contains(db)).collect(Collectors.toList());
        System.out.println(missinInIndex.size() + " accessions missing in the index.");
        System.out.println(missinInIndex.toString());
    }

    /**
     * Generates list of accessions not supposed to be in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public void getPresentInIndex(List<String> dbAcc, List<String> solrAcc) {
        List<String> presentInIndex = solrAcc.stream().filter(solr -> !dbAcc.contains(solr)).collect(Collectors.toList());
        System.out.println(presentInIndex.size() + " private accessions found in the index.");
        System.out.println(presentInIndex.toString());
    }
}
