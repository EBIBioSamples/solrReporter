package uk.ac.ebi.solrReporter.configuration;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by lucacherubin on 2016/05/25.
 */
@Configuration
public class RestTemplateConfiguration {

    @Value("${restTemplate.maxConnections}")
    private int maxConnections;

    @Value("${restTemplate.maxRoutes}")
    private int maxRoutes;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(getHttpClient()));
    }

    private CloseableHttpClient getHttpClient() {

        PoolingHttpClientConnectionManager connectionManager = getConnectionManager();
        ConnectionKeepAliveStrategy keepAliveStrategy = getKeepAliveStrategy();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setKeepAliveStrategy(keepAliveStrategy)
                .setConnectionManager(connectionManager)
                .build();

        return httpClient;
    }

    private PoolingHttpClientConnectionManager getConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager =  new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxRoutes);
        connectionManager.setValidateAfterInactivity(0);
        return connectionManager;
    }

    private ConnectionKeepAliveStrategy getKeepAliveStrategy() {
       return new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                //see if the user provides a live time
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                //default to one second live time
                return 1 * 1000;
            }
        };
    }

}
