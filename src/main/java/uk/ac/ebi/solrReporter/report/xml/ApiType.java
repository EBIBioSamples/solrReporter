package uk.ac.ebi.solrReporter.report.xml;

/**
 * Created by lucacherubin on 2016/05/09.
 */
public enum ApiType {

    LEGACY("https://www.ebi.ac.uk/biosamples"),
    NEW("");

    private String baseUrl;

    ApiType(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String url() {
        return baseUrl;
    }
}
