package uk.ac.ebi.solrReporter.report.tasks;

import org.jdom2.Document;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xmlunit.diff.Diff;
import uk.ac.ebi.solrReporter.report.xml.ApiType;

/**
 * Created by lucacherubin on 2016/05/26.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupXmlMatch extends XMLDocumentMatch{

    public GroupXmlMatch(String accession) {
        super(accession);
    }


    @Override
    protected Boolean documentMatching(String accession) throws Exception {

        Document legacyXml, novelXml;
        legacyXml = xmlApiService.getGroup(accession, ApiType.LEGACY);
        novelXml = solrSource.getGroupXML(accession);

        Diff diffEngine = getDifference(legacyXml,novelXml);

        return diffEngine.hasDifferences();
    }
}
