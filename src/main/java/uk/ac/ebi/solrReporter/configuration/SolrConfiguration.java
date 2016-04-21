package uk.ac.ebi.solrReporter.configuration;

import com.sun.istack.internal.NotNull;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@ConfigurationProperties("solr")
@EnableSolrRepositories(multicoreSupport = true)
public class SolrConfiguration {

    @NotNull
    private String host;

    public void setHost(String host) {
        this.host = host;
    }

    @Bean
    public SolrServer getSolrServer() {
        return new HttpSolrServer(host);
    }

}
