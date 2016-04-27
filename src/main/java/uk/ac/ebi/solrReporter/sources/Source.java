package uk.ac.ebi.solrReporter.sources;

import java.util.Set;

public interface Source {

    Set<String> getSamplesAccessions();

    Set<String> getGroupsAccessions();
}
