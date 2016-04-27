package uk.ac.ebi.solrReporter.report;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReportData {

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

    /* Validations */
    /**
     * Generates list of accessions missing in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public void getMissingInIndex(Set<String> dbAcc, Set<String> solrAcc) {
        Set<String> missinInIndex = dbAcc.stream().filter(db -> !solrAcc.contains(db)).collect(Collectors.toSet());
        System.out.println(missinInIndex.size() + " accessions missing in the index.");
        System.out.println(missinInIndex.toString());
    }

    /**
     * Generates list of accessions not supposed to be in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public void getPresentInIndex(Set<String> dbAcc, Set<String> solrAcc) {
        Set<String> presentInIndex = solrAcc.stream().filter(solr -> !dbAcc.contains(solr)).collect(Collectors.toSet());
        System.out.println(presentInIndex.size() + " private accessions found in the index.");
        System.out.println(presentInIndex.toString());
    }

    @Override
    public String toString() {
        return "Report{" +
                "samplesDB=" + getSamplesDBCount() +
                ", groupsDB=" + getGroupsDBCount() +
                ", samplesSolr=" + getSamplesSolrCount() +
                ", groupsSolr=" + getGroupsSolrCount() +
                ", samplesSolrMerged=" + getSamplesSolrMergedCount() +
                ", groupsSolrMerged=" + getGroupsSolrMergedCount() +
                '}';
    }
}
