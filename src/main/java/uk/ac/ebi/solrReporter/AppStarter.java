package uk.ac.ebi.solrReporter;

        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.boot.ApplicationArguments;
        import org.springframework.boot.ApplicationRunner;
        import org.springframework.boot.ExitCodeGenerator;
        import org.springframework.stereotype.Component;
        import uk.ac.ebi.solrReporter.report.Report;
        import uk.ac.ebi.solrReporter.report.ReportData;
        import uk.ac.ebi.solrReporter.report.XMLReport;
        import uk.ac.ebi.solrReporter.sources.SourceFactory;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Set;
        import java.util.concurrent.*;

@Component
public class AppStarter implements ApplicationRunner, ExitCodeGenerator {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XMLReport xmlReport;

    @Autowired
    private SourceFactory sourceFactory;

    @Autowired
    private Report report;

    private ExecutorService threadPool = null;
    private List<Future<?>> futures = new ArrayList<>();

    @Value("${threadPoolCount:4}")
    private int threadPoolCount;

    private int exitCode = 0;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting solrReporter");

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

            for (Future<?> future : futures) {
                future.get();
            }

            threadPool.shutdown();
            threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error collecting report data.", e);
            exitCode = 1;
            return;
        }

        log.debug(data.toString());

        Boolean xmlReportOk = xmlReport.generateReport(data);
        Boolean reportOK = report.generateReport(data);


        if (reportOK && xmlReportOk) {
            log.info("Report generated with no errors.");
        } else {
            log.error("There are errors in the report.");
            exitCode = 1;
            return;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}

