package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.sources.DBSourceImp;

@Component
public class AppStarter implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DBSourceImp dbSourceImp;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        dbSourceImp.getSamplesAccessions();

        dbSourceImp.getGroupsAccessions();

    }
}
