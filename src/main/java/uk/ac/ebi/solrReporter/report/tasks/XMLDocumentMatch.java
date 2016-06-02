package uk.ac.ebi.solrReporter.report.tasks;

import org.jdom2.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;
import uk.ac.ebi.solrReporter.report.xml.XmlApiService;
import uk.ac.ebi.solrReporter.report.xml.diffevaluators.IgnoreOrderDifferenceEvalutaor;
import uk.ac.ebi.solrReporter.report.xml.matchers.DatabaseNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.OrganizationNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.PersonNodeMatcher;
import uk.ac.ebi.solrReporter.report.xml.matchers.PublicationNodeMatcher;
import uk.ac.ebi.solrReporter.sources.SolrSource;

import java.util.concurrent.Callable;

/**
 * Created by lucacherubin on 2016/05/26.
 */
abstract class XMLDocumentMatch implements Callable<Boolean> {

    private String accession;

    @Autowired
    protected SolrSource solrSource;

    @Autowired
    protected XmlApiService xmlApiService;

    XMLDocumentMatch(String accession) {
        this.accession = accession;
    }

    public String getAccession() {
        return this.accession;
    }

    @Override
    public Boolean call() throws Exception {

        return documentMatching(accession);


    }

    protected abstract Boolean documentMatching(String accession) throws Exception;

    Diff getDifference(Document docA, Document docB) {

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
