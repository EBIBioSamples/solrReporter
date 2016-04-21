package uk.ac.ebi.solrReporter.sources;

import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolrSource implements Source {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SolrClient solrClient;

    @Override
    public List<String> getSamplesAccessions() {
        return null;
    }

    @Override
    public List<String> getGroupsAccessions() {
        return null;
    }
}
