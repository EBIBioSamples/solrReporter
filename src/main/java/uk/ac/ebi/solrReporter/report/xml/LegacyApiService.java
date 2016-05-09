package uk.ac.ebi.solrReporter.report.xml;

import org.jdom2.Document;

/**
 * Created by lucacherubin on 2016/05/09.
 */
public class LegacyApiService implements XmlApiService {
    @Override
    public Document getGroup(String accession) {
        return null;
    }

    @Override
    public Document getSample(String accession) {
        return null;
    }

    @Override
    public Document getSampleQuery(String query) {
        return null;
    }

    @Override
    public Document getGroupQuery(String query) {
        return null;
    }

    @Override
    public Document getSamplesInGroupQuery(String groupAccession, String query) {
        return null;
    }
}
