package uk.ac.ebi.solrReporter.report;

import java.util.Set;

/**
 * Created by lucacherubin on 2016/05/09.
 */
public class XMLReportData {

    private int sampleAccessionsCount;
    private Set<String> sampleAccessions;

    private int groupAccessionsCount;
    private Set<String> groupAccessions;

    private int queryStringCount;
    private Set<String> queryStrings;

    public Set<String> getQueryStrings() {
        return queryStrings;
    }

    public void setQueryStrings(Set<String> queryStrings) {
        this.queryStrings = queryStrings;
        this.queryStringCount = queryStrings.size();
    }

    private int getQueryStringCount() {
        return this.queryStringCount;
    }

    public Set<String> getGroupAccessions() {
        return groupAccessions;
    }

    public void setGroupAccessions(Set<String> groupAccessions) {
        this.groupAccessions = groupAccessions;
        this.groupAccessionsCount = groupAccessions.size();
    }

    public int getGroupAccessionsCount() {
        return groupAccessionsCount;
    }

    public Set<String> getSampleAccessions() {
        return sampleAccessions;
    }

    public void setSampleAccessions(Set<String> sampleAccessions) {
        this.sampleAccessions = sampleAccessions;
        this.sampleAccessionsCount = sampleAccessions.size();
    }

    public int getSampleAccessionsCount() {
        return sampleAccessionsCount;
    }

}
