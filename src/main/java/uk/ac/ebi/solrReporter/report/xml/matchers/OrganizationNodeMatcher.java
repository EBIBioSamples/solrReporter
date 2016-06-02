package uk.ac.ebi.solrReporter.report.xml.matchers;

import org.w3c.dom.Element;

/**
 * Created by lucacherubin on 19/01/2016.
 */
public class OrganizationNodeMatcher extends BaseElementSelector {

    @Override
    protected boolean areComparable(Element control, Element test) {

        String[] fields = {"Name","Address","URI","Email","Role"};
        return checkFieldsCorrespondence(control,test,fields);

    }
}
