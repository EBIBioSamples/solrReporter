package uk.ac.ebi.solrReporter.sources;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashMap;

@Service
public class SolrSource implements Source {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    HashMap<String, HttpSolrClient> solrClientMap;

    @Autowired
    private void setSolrClientMap(HashMap<String, HttpSolrClient> solrClientMap) {
        this.solrClientMap = solrClientMap;
    }

    @Override
    public List<String> getSamplesAccessions() {
        return null;
    }

    @Override
    public List<String> getGroupsAccessions() {
        return null;
    }
}
