package uk.ac.ebi.solrReporter.sources;

import java.util.List;

public interface Source {

    List<String> getSamplesAccessions();

    List<String> getGroupsAccessions();
}
