package uk.ac.ebi.solrReporter;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Report {

    private Set<String> samplesDB;

    private Set<String> groupsDB;

    private Set<String> samplesSolr;

    private Set<String> groupsSolr;

    private Set<String> samplesSolrMerged;

    private Set<String> groupsSolrMerged;

    public int getSamplesCountDB() {
        return samplesDB != null ? samplesDB.size() : 0;
    }

    public int getGroupsCountDB() {
        return groupsDB != null ? groupsDB.size() : 0;
    }

    public int getSamplesCountSolr() {
        return samplesSolr != null ? samplesSolr.size() : 0;
    }

    public int getGroupsCountSolr() {
        return groupsSolr != null ? groupsSolr.size() : 0;
    }

    public int getSamplesCountSolrMerged() {
        return samplesSolrMerged != null ? samplesSolrMerged.size() : 0;
    }

    public int getGroupsCountSolrMerged() {
        return groupsSolrMerged != null ? groupsSolrMerged.size() : 0;
    }

    /* Getters and Setters */
    public Set<String> getSamplesDB() {
        return samplesDB;
    }

    public void setSamplesDB(Set<String> samplesDB) {
        this.samplesDB = samplesDB;
    }

    public Set<String> getGroupsDB() {
        return groupsDB;
    }

    public void setGroupsDB(Set<String> groupsDB) {
        this.groupsDB = groupsDB;
    }

    public Set<String> getSamplesSolr() {
        return samplesSolr;
    }

    public void setSamplesSolr(Set<String> samplesSolr) {
        this.samplesSolr = samplesSolr;
    }

    public Set<String> getGroupsSolr() {
        return groupsSolr;
    }

    public void setGroupsSolr(Set<String> groupsSolr) {
        this.groupsSolr = groupsSolr;
    }

    public Set<String> getSamplesSolrMerged() {
        return samplesSolrMerged;
    }

    public void setSamplesSolrMerged(Set<String> samplesSolrMerged) {
        this.samplesSolrMerged = samplesSolrMerged;
    }

    public Set<String> getGroupsSolrMerged() {
        return groupsSolrMerged;
    }

    public void setGroupsSolrMerged(Set<String> groupsSolrMerged) {
        this.groupsSolrMerged = groupsSolrMerged;
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
                "samplesDB=" + getSamplesCountDB() +
                ", groupsDB=" + getGroupsCountDB() +
                ", samplesSolr=" + getSamplesCountSolr() +
                ", groupsSolr=" + getGroupsCountSolr() +
                ", samplesSolrMerged=" + getSamplesCountSolrMerged() +
                ", groupsSolrMerged=" + getGroupsCountSolrMerged() +
                '}';
    }
}
