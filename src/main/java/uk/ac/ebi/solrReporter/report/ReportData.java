package uk.ac.ebi.solrReporter.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Set;

@Component
public class ReportData {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private String DBSource;

    private String SolrSource;

    private int samplesDBCount;

    private int groupsDBCount;

    private int samplesSolrCount;

    private int groupsSolrCount;

    private int samplesSolrMergedCount;

    private int groupsSolrMergedCount;

    private Set<String> samplesDB;

    private Set<String> groupsDB;

    private Set<String> samplesSolr;

    private Set<String> groupsSolr;

    private Set<String> samplesSolrMerged;

    private Set<String> groupsSolrMerged;

    /* Getters and Setters */

    public String getDBSource() {
        return DBSource;
    }

    public String getSolrSource() {
        return SolrSource;
    }

    public int getSamplesDBCount() {
        return samplesDBCount;
    }

    public int getGroupsDBCount() {
        return groupsDBCount;
    }

    public int getSamplesSolrCount() {
        return samplesSolrCount;
    }

    public int getGroupsSolrCount() {
        return groupsSolrCount;
    }

    public int getSamplesSolrMergedCount() {
        return samplesSolrMergedCount;
    }

    public int getGroupsSolrMergedCount() {
        return groupsSolrMergedCount;
    }

    public Set<String> getSamplesDB() {
        return samplesDB;
    }

    public void setDBSource(String DBSource) {
        this.DBSource = DBSource;
    }

    public void setSolrSource(String solrSource) {
        SolrSource = solrSource;
    }

    public void setSamplesDB(Set<String> samplesDB) {
        this.samplesDB = samplesDB;
        this.samplesDBCount = this.samplesDB.size();
    }

    public Set<String> getGroupsDB() {
        return groupsDB;
    }

    public void setGroupsDB(Set<String> groupsDB) {
        this.groupsDB = groupsDB;
        this.groupsDBCount = this.groupsDB.size();
    }

    public Set<String> getSamplesSolr() {
        return samplesSolr;
    }

    public void setSamplesSolr(Set<String> samplesSolr) {
        this.samplesSolr = samplesSolr;
        this.samplesSolrCount = this.samplesSolr.size();
    }

    public Set<String> getGroupsSolr() {
        return groupsSolr;
    }

    public void setGroupsSolr(Set<String> groupsSolr) {
        this.groupsSolr = groupsSolr;
        this.groupsSolrCount = this.groupsSolr.size();
    }

    public Set<String> getSamplesSolrMerged() {
        return samplesSolrMerged;
    }

    public void setSamplesSolrMerged(Set<String> samplesSolrMerged) {
        this.samplesSolrMerged = samplesSolrMerged;
        this.samplesSolrMergedCount = this.samplesSolrMerged.size();
    }

    public Set<String> getGroupsSolrMerged() {
        return groupsSolrMerged;
    }

    public void setGroupsSolrMerged(Set<String> groupsSolrMerged) {
        this.groupsSolrMerged = groupsSolrMerged;
        this.groupsSolrMergedCount = this.groupsSolrMerged.size();
    }

    @Override
    public String toString() {
        DecimalFormat myFormat = new DecimalFormat("###,###.###");
        return "Report{" +
                "DBSource= " + getDBSource() +
                ", SolrSource= " + getSolrSource() +
                ", samplesDB= " + myFormat.format(getSamplesDBCount()) +
                ", groupsDB= " + myFormat.format(getGroupsDBCount()) +
                ", samplesSolr= " + myFormat.format(getSamplesSolrCount()) +
                ", groupsSolr= " + myFormat.format(getGroupsSolrCount()) +
                ", samplesSolrMerged= " + myFormat.format(getSamplesSolrMergedCount()) +
                ", groupsSolrMerged= " + myFormat.format(getGroupsSolrMergedCount()) +
                '}';
    }
}
