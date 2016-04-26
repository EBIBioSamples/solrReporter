package uk.ac.ebi.solrReporter.sources;

import com.sun.istack.internal.NotNull;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Configuration
@ConfigurationProperties("solr")
public class SolrSource implements Source {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @NotNull
    private String groups;
    @NotNull
    private String samples;
    @NotNull
    private String merged;

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setSamples(String samples) {
        this.samples = samples;
    }

    public void setMerged(String merged) {
        this.merged = merged;
    }

    @Override
    public List<String> getSamplesAccessions() {
        log.info("Getting samples accessions indexed to samples core.");
        HttpSolrClient samplesClient = new HttpSolrClient(samples);

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("q", "*:*");
        //solrParams.set("wt", "csv");
        solrParams.set("fl", "accession");

        QueryResponse response = null;

        try {
            response = samplesClient.query(solrParams);
        } catch (IOException | SolrServerException e) {
            log.error("Error querying " + samplesClient.getBaseURL() , e);
        }

        System.out.println("Response: " + response);

        return null;
    }

    @Override
    public List<String> getGroupsAccessions() {
        log.info("Getting groups accessions indexed to groups core.");
        HttpSolrClient groupsClient = new HttpSolrClient(groups);


        // TODO

        return null;
    }
}
