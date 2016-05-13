package uk.ac.ebi.solrReporter.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${threshold:100}")
    private int threshold;

    @Value("${filePath}")
    private String path;

    private DecimalFormat myFormat = new DecimalFormat("###,###.###");
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public boolean generateReport(ReportData data) {
        Boolean reportOK = true;
        Calendar cal = Calendar.getInstance();

        File file = new File(path + "report_" + dateFormat.format(cal.getTime()) + ".txt");
        File file_detail = new File(path + "report_detail_" + dateFormat.format(cal.getTime()) + ".txt");

        try {
            PrintWriter writer = new PrintWriter(file);
            PrintWriter detail_writer = new PrintWriter(file_detail);

            // DB vs SOLR
            // Groups
            writer.println("DB Source: " + data.getDBSource());
            writer.println("Solr Source: " + data.getSolrSource());
            writer.println();

            writer.println("Public groups found in DB:  " + myFormat.format(data.getGroupsDBCount()));
            writer.println("Groups found in Solr groups core:  " + myFormat.format(data.getGroupsSolrCount()));
            writer.println("Groups found in Solr merged core: " + myFormat.format(data.getGroupsSolrMergedCount()));
            if (data.getGroupsDBCount() != data.getGroupsSolrCount()) {
                int dif = data.getGroupsDBCount() - data.getGroupsSolrCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " groups between the DB and solr/groups.");
            }
            if (data.getGroupsDBCount() != data.getGroupsSolrMergedCount()) {
                int dif = data.getGroupsDBCount() - data.getGroupsSolrMergedCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " groups between the DB and solr/merged.");
            }
            writer.println();

            // Samples
            writer.println("Public samples found in DB: " + myFormat.format(data.getSamplesDBCount()));
            writer.println("Samples found in Solr samples core: " + myFormat.format(data.getSamplesSolrCount()));
            writer.println("Samples found in Solr merged core: " + myFormat.format(data.getSamplesSolrMergedCount()));
            if (data.getSamplesDBCount() != data.getSamplesSolrCount()) {
                int dif = data.getSamplesDBCount() - data.getSamplesSolrCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " samples between the DB and solr/samples.");
            }
            if (data.getSamplesDBCount() != data.getSamplesSolrMergedCount()) {
                int dif = data.getSamplesDBCount() - data.getSamplesSolrMergedCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                writer.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " samples between the DB and solr/merged.");
            }
            writer.println();

            // DETAIL
            detail_writer.println("Missing groups in groups core:");
            getMissingInIndex(data.getGroupsDB(), data.getGroupsSolr()).forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Private groups in groups core:");
            getPresentInIndex(data.getGroupsDB(), data.getGroupsSolr()).forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Missing samples in samples core:");
            getMissingInIndex(data.getSamplesDB(), data.getSamplesSolr()).forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Private samples in samples core:");
            getPresentInIndex(data.getSamplesDB(), data.getSamplesSolr()).forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Missing groups in merged core:");
            getMissingInIndex(data.getGroupsDB(), data.getGroupsSolrMerged()).forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Private groups in merged core:");
            getPresentInIndex(data.getGroupsDB(), data.getGroupsSolrMerged()).forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Missing samples in merged core:");
            getMissingInIndex(data.getSamplesDB(), data.getSamplesSolrMerged()).forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Private samples in merged core:");
            getPresentInIndex(data.getSamplesDB(), data.getSamplesSolrMerged()).forEach(detail_writer::println);
            detail_writer.println();

            // SOLR VS SOLR
            Set<String> groupsMissingMerged = getMissingInIndex(data.getGroupsSolr(), data.getGroupsSolrMerged());
            if (groupsMissingMerged != null && !groupsMissingMerged.isEmpty())
                writer.println("Missing groups in solr/merged when comparing to solr/groups: " + myFormat.format(Math.abs(groupsMissingMerged.size())));

            Set<String> samplesMissingMerged = getMissingInIndex(data.getSamplesSolr(), data.getSamplesSolrMerged());
            if (samplesMissingMerged != null && !samplesMissingMerged.isEmpty())
                writer.println("Missing samples in solr/merged when comparing to solr/samples: " + myFormat.format(Math.abs(samplesMissingMerged.size())));
            writer.println();

            Set<String> groupsPresentMerged = getPresentInIndex(data.getGroupsSolr(), data.getGroupsSolrMerged());
            if (groupsPresentMerged != null && !groupsPresentMerged.isEmpty())
                writer.println("Present groups in solr/merged when comparing to solr/groups: " + myFormat.format(Math.abs(groupsPresentMerged.size())));

            Set<String> samplesPresentMerged = getPresentInIndex(data.getSamplesSolr(), data.getSamplesSolrMerged());
            if (samplesPresentMerged != null && !samplesPresentMerged.isEmpty())
                writer.println("Present samples in solr/merged when comparing to solr/samples: " + myFormat.format(Math.abs(samplesPresentMerged.size())));
            writer.println();

            // DETAIL
            detail_writer.println("Missing groups in solr/merged when comparing to solr/groups");
            groupsMissingMerged.forEach(detail_writer::println);

            detail_writer.println("Missing samples in solr/merged when comparing to solr/samples");
            samplesMissingMerged.forEach(detail_writer::println);

            detail_writer.println("Present groups in solr/merged when comparing to solr/groups");
            groupsPresentMerged.forEach(detail_writer::println);
            detail_writer.println();

            detail_writer.println("Present samples in solr/merged when comparing to solr/samples");
            samplesPresentMerged.forEach(detail_writer::println);

            writer.flush();
            detail_writer.flush();

            writer.close();
            detail_writer.close();

        } catch (FileNotFoundException e) {
            log.error("Error while creating report", e);
            return false;
        }

        return reportOK;
    }

    /**
     * Generates list of accessions missing in the second list comparing with the first
     * @param first list of accessions
     * @param second list of accessions
     */
    public Set<String> getMissingInIndex(Set<String> first, Set<String> second) {
        Set<String> missingInIndex = new HashSet<>();
        if(first != null && second != null) {
            missingInIndex = first.parallelStream().filter(db -> !second.contains(db)).collect(Collectors.toSet());
        }
        return missingInIndex;
    }

    /**
     * Generates list of accessions present in the second list but not in the first
     * @param first list of accessions
     * @param second list of accessions
     */
    public Set<String> getPresentInIndex(Set<String> first, Set<String> second) {
        Set<String> presentInIndex = new HashSet<>();
        if(first != null && second != null) {
            presentInIndex = second.parallelStream().filter(solr -> !first.contains(solr)).collect(Collectors.toSet());
        }
        return presentInIndex;
    }
}
