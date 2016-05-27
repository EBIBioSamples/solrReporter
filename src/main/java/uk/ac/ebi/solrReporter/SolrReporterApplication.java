package uk.ac.ebi.solrReporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SolrReporterApplication {

	public static void main(String[] args) {
		//have to call system.exit to have spring generated exit code propagate correctly
		System.exit(SpringApplication.exit(SpringApplication.run(SolrReporterApplication.class, args)));
	}
}
