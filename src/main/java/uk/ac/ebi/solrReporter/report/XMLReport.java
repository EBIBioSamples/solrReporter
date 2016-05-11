package uk.ac.ebi.solrReporter.report;

import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXParseException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;
import uk.ac.ebi.solrReporter.report.xml.ApiType;
import uk.ac.ebi.solrReporter.report.xml.XmlApiService;
import uk.ac.ebi.solrReporter.report.xml.diffevaluators.IgnoreOrderDifferenceEvalutaor;
import uk.ac.ebi.solrReporter.report.xml.matchers.DatabaseNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.OrganizationNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.PersonNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.PublicationNodeMatcher;
import uk.ac.ebi.solrReporter.sources.SolrSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lucacherubin on 2016/05/09.
 */
@Component
public class XMLReport {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DecimalFormat myFormat = new DecimalFormat("###,###.###");
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private SolrSource solrSource;

    public boolean generateReport(ReportData data) {

        Calendar cal = Calendar.getInstance();
        String currentDate = dateFormat.format(cal.getTime());

        try (
                PrintWriter summary_writer =
                        new PrintWriter(new FileOutputStream(new File("report_" + currentDate + ".txt"), true));

                PrintWriter detail_writer =
                        new PrintWriter("xml_report_detail_" + currentDate + ".txt");

                PrintWriter parse_exception_writer =
                        new PrintWriter("xml_parse_exception_" + currentDate + ".txt" );
        ) {

            summary_writer.println("\n-- XML API Summary --\n");
            detail_writer.println("-- XML API Details --\n");

            // Temporary solution
            Set<String> groupAccessions = data.getGroupsDB();
            Set<String> sampleAccessions = data.getSamplesDB();


            Set<String> differentGroup = new HashSet<>();

            for (String accession: groupAccessions) {

                Document legacyXml, novelXml;
                try {
                    legacyXml = XmlApiService.getGroup(accession, ApiType.LEGACY);
                    novelXml = solrSource.getGroupXML(accession);
                } catch ( SAXParseException parseException) {
                    parse_exception_writer.println(String.format("Parse Exception: %s\n\t%s", accession, parseException.getMessage() ));
                    continue;
                } catch ( Exception e) {
                    log.error("Error while comparing grousp: ", e);
                    detail_writer.println("Error while comparing group:\t" + accession);
                    continue;
                }

                Diff diffEngine = getDifference(legacyXml,novelXml);

                if (diffEngine.hasDifferences()) {
                    detail_writer.println(String.format("\t%s",accession));
                    differentGroup.add(accession);

                }

            }

            summary_writer.println(String.format("Number of group compared:\t%d",groupAccessions.size()));
            summary_writer.println(String.format("Non matching groups:\t%d",differentGroup.size()));
            if (!differentGroup.isEmpty()) {
                detail_writer.println("Group Accessions not matching legacy version:");
                differentGroup.forEach(detail_writer::println);
            } else {
                detail_writer.println("All groups match their legacy versions");
            }

            Set<String> differentSample = new HashSet<>();

            for (String accession: sampleAccessions) {

                Document legacyXml, novelXml;
                try {
                    legacyXml = XmlApiService.getSample(accession, ApiType.LEGACY);
                    novelXml = solrSource.getSampleXML(accession);
                } catch ( SAXParseException parseException) {
                    parse_exception_writer.println(String.format("Parse Exception: %s\n\t%s", accession, parseException.getMessage() ));
                    continue;
                } catch ( Exception e) {
                    log.error("Error while comparing samples: ", e);
                    detail_writer.println("Error while comparing sample:\t" + accession);
                    continue;
                }

                Diff diffEngine = getDifference(legacyXml,novelXml);

                if (diffEngine.hasDifferences()) {
                    differentSample.add(accession);
                }

            }

            summary_writer.println(String.format("Number of samples compared:\t%d",sampleAccessions.size()));
            summary_writer.println(String.format("Non matching samples:\t%d",differentSample.size()));
            if (!differentSample.isEmpty()) {
                detail_writer.println("Samples Accessions not matching legacy version:");
                differentSample.forEach(detail_writer::println);
            } else {
                detail_writer.println("All samples match their legacy version");
            }

        } catch (FileNotFoundException e) {
            log.error("Error while creating report", e);
            return false;
        }

        return true;

    }

    private Diff getDifference(Document docA, Document docB) {

        return DiffBuilder.compare(docA)
                .withTest(docB)
                .checkForSimilar()
                .ignoreWhitespace()
                .normalizeWhitespace()
                .withNodeMatcher(getNodeMatcher())
                .withDifferenceEvaluator(new IgnoreOrderDifferenceEvalutaor())
                .build();
    }


    private NodeMatcher getNodeMatcher() {

        ElementSelector selector = ElementSelectors.conditionalBuilder()
                .whenElementIsNamed("Organization").thenUse(new OrganizationNodeMatcher())
                .whenElementIsNamed("Database").thenUse(new DatabaseNodeMatcher())
                .whenElementIsNamed("Publication").thenUse(new PublicationNodeMatcher())
                .whenElementIsNamed("Person").thenUse(new PersonNodeMatcher()).build();

        return new DefaultNodeMatcher(selector,ElementSelectors.byNameAndAllAttributes);

    }







}
