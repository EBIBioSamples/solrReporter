package uk.ac.ebi.solrReporter.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SolrReport implements Report {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${threshold:100}")
    private int threshold;

//    @Value("${filePath:./}")
//    private File path;

    private DecimalFormat myFormat = new DecimalFormat("###,###.###");

    public boolean generateReport(ReportData data, File summary, File details) {
        Boolean reportOK = true;
//        Calendar cal = Calendar.getInstance();
        
//        if (!path.isDirectory()) {
//        	throw new IllegalArgumentException("filePath must be an existing directory");
//        }
//
//        File file = new File(path,"report_" + dateFormat.format(cal.getTime()) + ".txt");
//        File file_detail = new File(path,"report_detail_" + dateFormat.format(cal.getTime()) + ".txt");

        try {
//            PrintWriter writer = new PrintWriter(file);
//            PrintWriter detail_writer = new PrintWriter(file_detail);
            PrintWriter summaryWriter = new PrintWriter(getAppender(summary));
            PrintWriter detailsWriter = new PrintWriter(getAppender(details));

            // DB vs SOLR
            // Groups
            summaryWriter.println("DB Source: " + data.getDBSource());
            summaryWriter.println("Solr Source: " + data.getSolrSource());
            summaryWriter.println();

            summaryWriter.println("Public groups found in DB:  " + myFormat.format(data.getGroupsDBCount()));
            summaryWriter.println("Groups found in Solr groups core:  " + myFormat.format(data.getGroupsSolrCount()));
            summaryWriter.println("Groups found in Solr merged core: " + myFormat.format(data.getGroupsSolrMergedCount()));
            if (data.getGroupsDBCount() != data.getGroupsSolrCount()) {
                int dif = data.getGroupsDBCount() - data.getGroupsSolrCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                summaryWriter.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " groups between the DB and solr/groups.");
            }
            if (data.getGroupsDBCount() != data.getGroupsSolrMergedCount()) {
                int dif = data.getGroupsDBCount() - data.getGroupsSolrMergedCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                summaryWriter.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " groups between the DB and solr/merged.");
            }
            summaryWriter.println();

            // Samples
            summaryWriter.println("Public samples found in DB: " + myFormat.format(data.getSamplesDBCount()));
            summaryWriter.println("Samples found in Solr samples core: " + myFormat.format(data.getSamplesSolrCount()));
            summaryWriter.println("Samples found in Solr merged core: " + myFormat.format(data.getSamplesSolrMergedCount()));
            if (data.getSamplesDBCount() != data.getSamplesSolrCount()) {
                int dif = data.getSamplesDBCount() - data.getSamplesSolrCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                summaryWriter.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " samples between the DB and solr/samples.");
            }
            if (data.getSamplesDBCount() != data.getSamplesSolrMergedCount()) {
                int dif = data.getSamplesDBCount() - data.getSamplesSolrMergedCount();
                if (Math.abs(dif) > threshold)
                    reportOK = false;
                summaryWriter.println("  There's a difference of " + myFormat.format(Math.abs(dif)) + " samples between the DB and solr/merged.");
            }
            summaryWriter.println();

            // DETAIL
            Set<String> set;

            detailsWriter.println("Missing groups in groups core:");
            set = getMissingInIndex(data.getGroupsDB(), data.getGroupsSolr());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Private groups in groups core:");
            set = getPresentInIndex(data.getGroupsDB(), data.getGroupsSolr());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Missing samples in samples core:");
            set = getMissingInIndex(data.getSamplesDB(), data.getSamplesSolr());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Private samples in samples core:");
            set = getPresentInIndex(data.getSamplesDB(), data.getSamplesSolr());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Missing groups in merged core:");
            set = getMissingInIndex(data.getGroupsDB(), data.getGroupsSolrMerged());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Private groups in merged core:");
            set = getPresentInIndex(data.getGroupsDB(), data.getGroupsSolrMerged());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Missing samples in merged core:");
            set = getMissingInIndex(data.getSamplesDB(), data.getSamplesSolrMerged());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Private samples in merged core:");
            set = getPresentInIndex(data.getSamplesDB(), data.getSamplesSolrMerged());
            set.forEach(detailsWriter::println);
            if (set.size() > threshold) reportOK = false;
            detailsWriter.println();

            // SOLR VS SOLR
            Set<String> groupsMissingMerged = getMissingInIndex(data.getGroupsSolr(), data.getGroupsSolrMerged());
            if (groupsMissingMerged != null && !groupsMissingMerged.isEmpty()) {
                summaryWriter.println("Missing groups in solr/merged when comparing to solr/groups: " + myFormat.format(Math.abs(groupsMissingMerged.size())));
                reportOK = false;
            }

            Set<String> samplesMissingMerged = getMissingInIndex(data.getSamplesSolr(), data.getSamplesSolrMerged());
            if (samplesMissingMerged != null && !samplesMissingMerged.isEmpty()) {
                summaryWriter.println("Missing samples in solr/merged when comparing to solr/samples: " + myFormat.format(Math.abs(samplesMissingMerged.size())));
                reportOK = false;
            }
            summaryWriter.println();

            Set<String> groupsPresentMerged = getPresentInIndex(data.getGroupsSolr(), data.getGroupsSolrMerged());
            if (groupsPresentMerged != null && !groupsPresentMerged.isEmpty()) {
                summaryWriter.println("Present groups in solr/merged when comparing to solr/groups: " + myFormat.format(Math.abs(groupsPresentMerged.size())));
                reportOK = false;
            }

            Set<String> samplesPresentMerged = getPresentInIndex(data.getSamplesSolr(), data.getSamplesSolrMerged());
            if (samplesPresentMerged != null && !samplesPresentMerged.isEmpty()) {
                summaryWriter.println("Present samples in solr/merged when comparing to solr/samples: " + myFormat.format(Math.abs(samplesPresentMerged.size())));
                reportOK = false;
            }
            summaryWriter.println();

            // DETAIL
            detailsWriter.println("Missing groups in solr/merged when comparing to solr/groups");
            groupsMissingMerged.forEach(detailsWriter::println);
            if (groupsMissingMerged.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Missing samples in solr/merged when comparing to solr/samples");
            samplesMissingMerged.forEach(detailsWriter::println);
            if (samplesMissingMerged.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Present groups in solr/merged when comparing to solr/groups");
            groupsPresentMerged.forEach(detailsWriter::println);
            if (groupsPresentMerged.size() > threshold) reportOK = false;
            detailsWriter.println();

            detailsWriter.println("Present samples in solr/merged when comparing to solr/samples");
            samplesPresentMerged.forEach(detailsWriter::println);
            if (samplesPresentMerged.size() > threshold) reportOK = false;

            summaryWriter.flush();
            detailsWriter.flush();

            summaryWriter.close();
            detailsWriter.close();

        } catch (IOException e) {
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
