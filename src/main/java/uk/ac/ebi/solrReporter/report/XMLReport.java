package uk.ac.ebi.solrReporter.report;

import org.jdom2.Document;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelector;
import org.xmlunit.diff.ElementSelectors;
import uk.ac.ebi.solrReporter.report.xml.ApiType;
import uk.ac.ebi.solrReporter.report.xml.XmlApiService;
import uk.ac.ebi.solrReporter.report.xml.diffevaluators.IgnoreOrderDifferenceEvalutaor;
import uk.ac.ebi.solrReporter.report.xml.matchers.DatabaseNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.OrganizationNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.PersonNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.PublicationNodeMatcher;

import java.util.Set;

/**
 * Created by lucacherubin on 2016/05/09.
 */
public class XMLReport {

    public boolean generateReport(ReportData data) {

        Set<String> groupAccessions = data.getGroupsSolr();
        Set<String> sampleAccessions = data.getSamplesSolr();

        ElementSelector selector = ElementSelectors.conditionalBuilder()
                    .whenElementIsNamed("Organization").thenUse(new OrganizationNodeMatcher())
                    .whenElementIsNamed("Database").thenUse(new DatabaseNodeMatcher())
                    .whenElementIsNamed("Publication").thenUse(new PublicationNodeMatcher())
                    .whenElementIsNamed("Person").thenUse(new PersonNodeMatcher()).build();

        for (String accession: sampleAccessions) {
            Document legacyXml, novelXml;
            try {
                legacyXml = XmlApiService.getSample(accession, ApiType.LEGACY);
                novelXml = XmlApiService.getSample(accession, ApiType.NEW);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Diff diffEngine = DiffBuilder
                    .compare(novelXml)
                    .withTest(legacyXml)
                    .checkForSimilar()
                    .ignoreWhitespace()
                    .normalizeWhitespace()
                    .withDifferenceEvaluator(new IgnoreOrderDifferenceEvalutaor())
                    .build();

            if (diffEngine.hasDifferences()) {
                //TODO Do something

            }



        }



    }



}
