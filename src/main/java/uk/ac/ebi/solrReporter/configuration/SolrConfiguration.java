package uk.ac.ebi.solrReporter.configuration;

import com.sun.istack.internal.NotNull;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@ConfigurationProperties("solr")
public class SolrConfiguration {

    @NotNull
    private String samplesUrl;

    @NotNull
    private String groupsUrl;

    @NotNull
    private String mergedUrl;

    public void setSamplesUrl(String samplesUrl) {
        this.samplesUrl = samplesUrl;
    }

    public void setGroupsUrl(String groupsUrl) {
        this.groupsUrl = groupsUrl;
    }

    public void setMergedUrl(String mergedUrl) {
        this.mergedUrl = mergedUrl;
    }

    @Bean
    public HashMap<String, HttpSolrClient> solrInstances() {
        HashMap<String, HttpSolrClient> map = new HashMap<>();
        map.put("samples", new HttpSolrClient(samplesUrl));
        map.put("groups", new HttpSolrClient(groupsUrl));
        map.put("merged", new HttpSolrClient(mergedUrl));
        return map;
    }

}
