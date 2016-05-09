package uk.ac.ebi.solrReporter.sources;

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
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.toIntExact;

@Service
@Configuration
@ConfigurationProperties("solr")
public class SolrSource implements Source {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private String groups;
    private String samples;
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
    public Set<String> getSamplesAccessions() {
        log.info("Getting samples accessions indexed to samples core.");
        HttpSolrClient samplesClient = new HttpSolrClient(samples);

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("q", "*:*");
        solrParams.set("fl", "accession");
        solrParams.set("rows", 1);

        QueryResponse response = null;
        try {
            response = samplesClient.query(solrParams);
            log.debug("First response: " + response);
        } catch (IOException | SolrServerException e) {
            log.error("Error querying " + samplesClient.getBaseURL() , e);
        }

        if (response != null) {
            return doSecondQuery(response, solrParams, samplesClient);
        } else {
            return new HashSet<>();
        }
    }

    @Override
    public Set<String> getGroupsAccessions() {
        log.info("Getting groups accessions indexed to groups core.");
        HttpSolrClient groupsClient = new HttpSolrClient(groups);

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("q", "*:*");
        solrParams.set("fl", "accession");
        solrParams.set("rows", 1);

        QueryResponse response = null;
        try {
            response = groupsClient.query(solrParams);
            log.debug("First response: " + response);
        } catch (IOException | SolrServerException e) {
            log.error("Error querying " + groupsClient.getBaseURL() , e);
        }

        if (response != null) {
            return doSecondQuery(response, solrParams, groupsClient);
        } else {
            return new HashSet<>();
        }
    }

    public Set<String> getSamplesFromMergedCore() {
        log.info("Getting sample accessions indexed to merged core.");
        return getFromMergedCore("sample");
    }

    public Set<String> getGroupsFromMergedCore() {
        log.info("Getting group accessions indexed to merged core.");
        return getFromMergedCore("group");
    }

    private Set<String> getFromMergedCore(String content_type) {
        HttpSolrClient mergedClient = new HttpSolrClient(merged);

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams
                .set("q", "content_type:" + content_type)
                .set("fl", "accession")
                .set("rows", 1);

        QueryResponse response = null;
        try {
            response = mergedClient.query(solrParams);
            log.debug("First response: " + response);
        } catch (IOException | SolrServerException e) {
            log.error("Error querying " + mergedClient.getBaseURL() , e);
        }

        if (response != null) {
            return doSecondQuery(response, solrParams, mergedClient);
        } else {
            return new HashSet<>();
        }
    }

    private Set<String> doSecondQuery(QueryResponse response, ModifiableSolrParams solrParams, HttpSolrClient solrClient) {
        Set<String> accessions = new HashSet<>();
        if ((Integer)response.getHeader().get("status") == 0) {

            solrParams.set("rows", toIntExact(response.getResults().getNumFound()));
            try {
                response = solrClient.query(solrParams);
            } catch (IOException | SolrServerException e) {
                log.error("Error querying " + solrClient.getBaseURL() , e);
            }
            log.debug("Second response head: " + response.getHeader());

            // Add accessions to result list
            response.getResults().forEach(row -> accessions.add((String) row.getFieldValue("accession")));
            log.info("Successfully fetched " + accessions.size()+ " from solr.");

        } else {
            log.error("Error querying Samples core got status: " + response.getHeader().get("status"));
            System.exit((Integer) response.getHeader().get("status"));
        }

        return accessions;
    }
}
