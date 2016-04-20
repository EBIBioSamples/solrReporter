package uk.ac.ebi.solrReporter.sources;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface Source {

    List<String> getSamplesAccessions();

    List<String> getGroupsAccessions();
}
