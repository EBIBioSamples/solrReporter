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
public interface XmlApiService {

    Document getGroup(String accession);

    Document getSample(String accession);

    Document getSampleQuery(String query);

    Document getGroupQuery(String query);

    Document getSamplesInGroupQuery(String groupAccession, String query);





    default Document buildDocumentFromUrl(String url) throws Exception {
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
