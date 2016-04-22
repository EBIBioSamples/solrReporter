package uk.ac.ebi.solrReporter.sources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SourceFactoryImp implements SourceFactory {

    @Autowired
    private DBSource dbSource;

    @Autowired
    private SolrSource solrSource;

    @Override
    public DBSource getDBSource() {
        return this.dbSource;
    }

    @Override
    public SolrSource getSolrSource() {
        return this.solrSource;
    }
}
