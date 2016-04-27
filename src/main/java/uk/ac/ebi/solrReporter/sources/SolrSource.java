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
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

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
        solrParams.set("fl", "accession");
        solrParams.set("rows", 1);

        QueryResponse response;
        try {
            response = samplesClient.query(solrParams);
        } catch (IOException | SolrServerException e) {
            log.error("Error querying " + samplesClient.getBaseURL() , e);
            return new ArrayList<>();
        }

        return doSecondQuery(response, solrParams, samplesClient);
    }

    @Override
    public List<String> getGroupsAccessions() {
        log.info("Getting groups accessions indexed to groups core.");
        HttpSolrClient groupsClient = new HttpSolrClient(groups);

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("q", "*:*");
        solrParams.set("fl", "accession");
        solrParams.set("rows", 1);

        QueryResponse response;
        try {
            response = groupsClient.query(solrParams);
        } catch (IOException | SolrServerException e) {
            log.error("Error querying " + groupsClient.getBaseURL() , e);
            return new ArrayList<>();
        }

        return doSecondQuery(response, solrParams, groupsClient);
    }

    private List<String> doSecondQuery(QueryResponse response, ModifiableSolrParams solrParams, HttpSolrClient solrClient) {
        List<String> accessions = new ArrayList<>();
        if ((Integer)response.getHeader().get("status") == 0) {
            log.debug("First response: " + response);

            solrParams.set("rows", toIntExact(response.getResults().getNumFound()));
            try {
                response = solrClient.query(solrParams);
            } catch (IOException | SolrServerException e) {
                log.error("Error querying " + solrClient.getBaseURL() , e);
            }
            log.debug("Second response head: " + response.getHeader());

            // Add accessions to result list
            response.getResults().forEach(row -> accessions.add((String) row.getFieldValue("accession")));

        } else {
            log.error("Error querying Samples core got status: " + response.getHeader().get("status"));
            System.exit((Integer) response.getHeader().get("status"));
        }

        return accessions;
    }
}
