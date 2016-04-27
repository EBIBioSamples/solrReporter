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

        // From DB - samples
        Set<String> samplesFromDB = sourceFactory.getDBSource().getSamplesAccessions();
        log.info("Found " + samplesFromDB.size() + " samples in th DB.");

        // From DB - groups
        Set<String> groupsFromDB = sourceFactory.getDBSource().getGroupsAccessions();
        log.info("Found " + groupsFromDB.size() + " groups in th DB.");

        // From Solr - samples
        Set<String> samplesFromSolr = sourceFactory.getSolrSource().getSamplesAccessions();
        log.info("Found " + samplesFromSolr.size() + " samples in the solr index.");

        // From Solr - groups
        Set<String> groupsFromSolr = sourceFactory.getSolrSource().getGroupsAccessions();
        log.info("Found " + groupsFromSolr.size() + " groups in the solr index.");

        // From Solr - merged (samples)
        Set<String> samplesFromMergedSolr =  sourceFactory.getSolrSource().getSamplesFromMergedCore();
        log.info("Found " + samplesFromMergedSolr.size() + " samples in the merged solr index.");

        // From Solr - merged (groups)
        Set<String> groupsFromMergedCore =  sourceFactory.getSolrSource().getGroupsFromMergedCore();
        log.info("Found " + groupsFromMergedCore.size() + " groups in the merged solr index.");

    }

    /**
     * Generates list of accessions missing in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public void getMissingInIndex(Set<String> dbAcc, Set<String> solrAcc) {
        Set<String> missinInIndex = dbAcc.stream().filter(db -> !solrAcc.contains(db)).collect(Collectors.toSet());
        System.out.println(missinInIndex.size() + " accessions missing in the index.");
        System.out.println(missinInIndex.toString());
    }

    /**
     * Generates list of accessions not supposed to be in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public void getPresentInIndex(Set<String> dbAcc, Set<String> solrAcc) {
        Set<String> presentInIndex = solrAcc.stream().filter(solr -> !dbAcc.contains(solr)).collect(Collectors.toSet());
        System.out.println(presentInIndex.size() + " private accessions found in the index.");
        System.out.println(presentInIndex.toString());
    }
}
