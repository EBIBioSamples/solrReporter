package uk.ac.ebi.solrReporter.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Report {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DecimalFormat myFormat = new DecimalFormat("###,###.###");
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public boolean generateReport(ReportData data) {
        Calendar cal = Calendar.getInstance();

        try (PrintWriter writer = new PrintWriter("report_" + dateFormat.format(cal.getTime()) + ".txt")) {

            // GROUPS
            writer.println("Public groups found in DB:  " + myFormat.format(data.getGroupsDBCount()));
            writer.println("Groups found in Solr groups core:  " + myFormat.format(data.getGroupsSolrCount()));
            writer.println("Groups found in Solr merged core: " + myFormat.format(data.getGroupsSolrMergedCount()));
            if (data.getGroupsDBCount() != data.getGroupsSolrCount()) {
                int dif = data.getGroupsDBCount() - data.getGroupsSolrCount();
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " groups between the DB and solr/groups.");
            }
            if (data.getGroupsDBCount() != data.getGroupsSolrMergedCount()) {
                int dif = data.getGroupsDBCount() - data.getGroupsSolrMergedCount();
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " groups between the DB and solr/merged.");
            }
            writer.println();

            // SAMPLES
            writer.println("Public samples found in DB: " + myFormat.format(data.getSamplesDBCount()));
            writer.println("Samples found in Solr samples core: " + myFormat.format(data.getSamplesSolrCount()));
            writer.println("Samples found in Solr merged core: " + myFormat.format(data.getSamplesSolrMergedCount()));
            if (data.getSamplesDBCount() != data.getSamplesSolrCount()) {
                int dif = data.getSamplesDBCount() - data.getSamplesSolrCount();
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " samples between the DB and solr/samples.");
            }
            if (data.getSamplesDBCount() != data.getSamplesSolrMergedCount()) {
                int dif = data.getSamplesDBCount() - data.getSamplesSolrMergedCount();
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " samples between the DB and solr/merged.");
            }
            writer.println();

            // DETAIL
            writer.println("Missing groups in groups core:");
            getMissingInIndex(data.getGroupsDB(), data.getGroupsSolr()).forEach(writer::println);
            writer.println("Private groups in groups core:");
            getPresentInIndex(data.getGroupsDB(), data.getGroupsSolr()).forEach(writer::println);
            writer.println();

            writer.println("Missing samples in samples core:");
            getMissingInIndex(data.getSamplesDB(), data.getSamplesSolr()).forEach(writer::println);
            writer.println("Private samples in samples core:");
            getPresentInIndex(data.getSamplesDB(), data.getSamplesSolr()).forEach(writer::println);
            writer.println();

            writer.println("Missing groups in merged core:");
            getMissingInIndex(data.getGroupsDB(), data.getGroupsSolrMerged()).forEach(writer::println);
            writer.println("Private groups in merged core:");
            getPresentInIndex(data.getGroupsDB(), data.getGroupsSolrMerged()).forEach(writer::println);
            writer.println("Missing samples in merged core:");
            getMissingInIndex(data.getSamplesDB(), data.getSamplesSolrMerged()).forEach(writer::println);
            writer.println("Private samples in merged core:");
            getPresentInIndex(data.getSamplesDB(), data.getSamplesSolrMerged()).forEach(writer::println);

            writer.flush();

        } catch (FileNotFoundException e) {
            log.error("Error creating report txt file", e);
            return false;
        }

        return true;
    }

    /**
     * Generates list of accessions missing in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public Set<String> getMissingInIndex(Set<String> dbAcc, Set<String> solrAcc) {
        Set<String> missingInIndex = new HashSet<>();
        if(dbAcc != null && solrAcc != null) {
            missingInIndex = dbAcc.parallelStream().filter(db -> !solrAcc.contains(db)).collect(Collectors.toSet());
        }
        return missingInIndex;
    }

    /**
     * Generates list of accessions not supposed to be in the solr index
     * @param dbAcc accessions from DB
     * @param solrAcc accessions from solr
     */
    public Set<String> getPresentInIndex(Set<String> dbAcc, Set<String> solrAcc) {
        Set<String> presentInIndex = new HashSet<>();
        if(dbAcc != null && solrAcc != null) {
            presentInIndex = solrAcc.parallelStream().filter(solr -> !dbAcc.contains(solr)).collect(Collectors.toSet());
        }
        return presentInIndex;
    }
}
