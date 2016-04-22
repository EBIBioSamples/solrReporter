package uk.ac.ebi.solrReporter.sources;

import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolrSource implements Source {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SolrClient solrClient;

    private SolrTemplate solrTemplate = null;

    @Override
    public List<String> getSamplesAccessions() {
        log.info("Getting samples accessions indexed to samples core.");

        solrTemplate = new SolrTemplate(solrClient, "samples");

        // TODO

        log.info("Successfully fetched " + "" + " accessions from solr " + solrTemplate.getSolrCore() + " core.");
        return null;
    }

    @Override
    public List<String> getGroupsAccessions() {
        log.info("Getting groups accessions indexed to groups core.");

        solrTemplate = new SolrTemplate(solrClient, "groups");

        // TODO

        log.info("Successfully fetched " + "" + " accessions from solr " + solrTemplate.getSolrCore() + " core.");
        return null;
    }
}
