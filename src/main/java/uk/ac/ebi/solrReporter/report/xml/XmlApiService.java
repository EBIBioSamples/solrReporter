package uk.ac.ebi.solrReporter.report.xml;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

/**
 * Created by lucacherubin on 2016/05/09.
 */
@Service
public class XmlApiService {

    @Autowired
    RestTemplate restTemplate;

    private XmlApiService() {}

    public Document getGroup(String accession, ApiType api) throws Exception {
        String url = String.format("%s/group/%s", api.url(), accession);
        return buildDocumentFromUrl(url);
    }

    public Document getSample(String accession, ApiType api) throws Exception {
        String url = String.format("%s/sample/%s", api.url(), accession);
        return buildDocumentFromUrl(url);
    }

    public Document getSampleQuery(String query, ApiType api) throws Exception {
        String url = String.format("%s/sample/query=%s", api.url(), query);
        return buildDocumentFromUrl(url);
    }

    public Document getGroupQuery(String query, ApiType api) throws Exception {
        String url = String.format("%s/group/query=%s", api.url(), query);
        return buildDocumentFromUrl(url);
    }

    public Document getSamplesInGroupQuery(String groupAccession, String query, ApiType api) throws Exception {
        String url = String.format("%s/groupsamples/%s/query=%s", api.url(), groupAccession, query);
        return buildDocumentFromUrl(url);
    }

    private Document buildDocumentFromUrl(String url) throws Exception {
//        URL urlObj = new URL(url);
//        URLConnection uc = urlObj.openConnection();
//        HttpURLConnection connection = (HttpURLConnection) uc;
//
//        InputStream in = connection.getInputStream();
        ResponseEntity<String> response = restTemplate.getForEntity(url,String.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new IOException("Problem getting HTTP response " + response.getStatusCode());
        }

        String body = response.getBody();

        SAXBuilder parser = new SAXBuilder();
        return parser.build(new StringReader(body));


    }


}
