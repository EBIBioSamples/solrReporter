package uk.ac.ebi.solrReporter.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.solr.client.solrj.SolrServer;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolrSource implements Source {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SolrServer solrServer;

    private SolrTemplate solrTemplate = null;

    @Override
    public List<String> getSamplesAccessions() {
        solrTemplate = new SolrTemplate(solrServer, "samples");
        System.out.println(solrTemplate.getSolrCore());
        return null;
    }

    @Override
    public List<String> getGroupsAccessions() {
        solrTemplate = new SolrTemplate(solrServer, "groups");
        System.out.println(solrTemplate.getSolrCore());
        return null;
    }
}
