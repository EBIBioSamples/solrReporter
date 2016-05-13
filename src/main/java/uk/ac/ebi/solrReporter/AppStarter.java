package uk.ac.ebi.solrReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.report.Report;
import uk.ac.ebi.solrReporter.report.ReportData;
import uk.ac.ebi.solrReporter.sources.SourceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class AppStarter implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceFactory sourceFactory;

    @Autowired
    private Report report;

    private ExecutorService threadPool = null;
    private List<Future<?>> futures = new ArrayList<>();

    @Value("${threadPoolCount}")
    private int threadPoolCount;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting Report");

        threadPool = Executors.newFixedThreadPool(threadPoolCount);
        ReportData data = new ReportData();

        data.setDBSource(sourceFactory.getDBSource().getSourceUrl());
        data.setSolrSource("[" + sourceFactory.getSolrSource().getGroups() + ", " +
                sourceFactory.getSolrSource().getSamples() + ", " +
                sourceFactory.getSolrSource().getMerged() + "]");

        try {
            futures.add(threadPool.submit(() -> data.setSamplesDB(sourceFactory.getDBSource().getSamplesAccessions())));
            futures.add(threadPool.submit(() -> data.setGroupsDB(sourceFactory.getDBSource().getGroupsAccessions())));
            futures.add(threadPool.submit(() -> data.setGroupsSolr(sourceFactory.getSolrSource().getGroupsAccessions())));
            futures.add(threadPool.submit(() -> data.setGroupsSolrMerged(sourceFactory.getSolrSource().getGroupsFromMergedCore())));

            data.setSamplesSolr(sourceFactory.getSolrSource().getSamplesAccessions());
            data.setSamplesSolrMerged(sourceFactory.getSolrSource().getSamplesFromMergedCore());

            for (int i = 0; i < futures.size(); i++) {
                futures.get(i).get();
            }

            threadPool.shutdown();
            threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error collecting report data.", e);
            System.exit(1);
        }

        log.info(data.toString());

        Boolean reportOK = report.generateReport(data);

        if (reportOK) {
            log.info("Report generated successfully!");
        } else {
            log.error("Failed to generate report!");
        }
    }
}
