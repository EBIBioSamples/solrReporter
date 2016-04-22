package uk.ac.ebi.solrReporter.sources;

public interface SourceFactory {

    DBSource getDBSource();

    SolrSource getSolrSource();
}
