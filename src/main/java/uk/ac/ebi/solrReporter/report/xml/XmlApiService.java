package uk.ac.ebi.solrReporter.report.xml;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by lucacherubin on 2016/05/09.
 */
public class XmlApiService {

    public static Document getGroup(String accession, ApiType api) throws Exception {
        String url = String.format("%s/group/%s", api.url(), accession);
        return buildDocumentFromUrl(url);
    }

    public static Document getSample(String accession, ApiType api) throws Exception {
        String url = String.format("%s/sample/%s", api.url(), accession);
        return buildDocumentFromUrl(url);
    }

    public static Document getSampleQuery(String query, ApiType api) throws Exception {
        String url = String.format("%s/sample/query=%s", api.url(), query);
        return buildDocumentFromUrl(url);
    }

    public static Document getGroupQuery(String query, ApiType api) throws Exception {
        String url = String.format("%s/group/query=%s", api.url(), query);
        return buildDocumentFromUrl(url);
    }

    public static Document getSamplesInGroupQuery(String groupAccession, String query, ApiType api) throws Exception {
        String url = String.format("%s/groupsamples/%s/query=%s", api.url(), groupAccession, query);
        return buildDocumentFromUrl(url);
    }

    public static Document buildDocumentFromUrl(String url) throws Exception {
        URL urlObj = new URL(url);
        URLConnection uc = urlObj.openConnection();
        HttpURLConnection connection = (HttpURLConnection) uc;

        InputStream in = connection.getInputStream();
        SAXBuilder parser = new SAXBuilder();
        Document xmlDocument = parser.build(in);
        in.close();

        return xmlDocument;
    }
}
