package uk.ac.ebi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.solrReporter.SolrReporterApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SolrReporterApplication.class)
public class SolrReporterApplicationTests {

	@Test
	public void contextLoads() {
	}

}
