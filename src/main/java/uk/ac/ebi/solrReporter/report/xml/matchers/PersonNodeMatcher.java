package uk.ac.ebi.solrReporter.report.xml.matchers;

import org.w3c.dom.Element;

/**
 * Created by lucacherubin on 21/01/2016.
 */
public class PersonNodeMatcher extends BaseElementSelector {

    @Override
    protected boolean areComparable(Element control, Element test) {

        String[] fields = {"FirstName","LastName","Email","MidInitial","Role"};
        return checkFieldsCorrespondence(control,test,fields);


    }
}
