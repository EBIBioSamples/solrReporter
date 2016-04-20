package uk.ac.ebi.solrReporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class SolrReporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppStarter.class, args);
	}
}
