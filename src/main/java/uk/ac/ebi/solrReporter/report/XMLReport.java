package uk.ac.ebi.solrReporter.report;

import org.jdom2.input.JDOMParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import uk.ac.ebi.solrReporter.report.tasks.GroupXmlMatch;
import uk.ac.ebi.solrReporter.report.tasks.SampleXmlMatch;
import uk.ac.ebi.solrReporter.report.tasks.SingleCheck;
import uk.ac.ebi.solrReporter.report.xml.XmlApiService;
import uk.ac.ebi.solrReporter.sources.SolrSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Created by lucacherubin on 2016/05/09.
 */
@Component
public class XMLReport implements Report{

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DecimalFormat myFormat = new DecimalFormat("###,###.###");

    @Autowired
    private ApplicationContext context;

    @Value("${threadPoolCount}")
    private int threadPoolCount;

    @Value("${reporter.xml.testPerformance:true}")
    private boolean testPerformance;

    private ExecutorService threadPool = null;
    public boolean generateReport(ReportData data, File summary, File details) {

        final int checkNumber = threadPoolCount * 125; // maintain constant the ratio between number of checked samples and threads
        boolean reportStatus = false;

        threadPool = Executors.newFixedThreadPool(threadPoolCount);

        long startTime = System.nanoTime();

        PrintWriter summaryWriter = null;
        PrintWriter detailWriter = null;

        try  {

            summaryWriter = new PrintWriter(getAppender(summary));
            detailWriter = new PrintWriter(getAppender(details));

            summaryWriter.println("\n-- XML API Summary --\n");
            detailWriter.println("\n-- XML API Details --\n");

            // Temporary solution
            Set<String> groupAccessions = data.getGroupsDB();
            Set<String> sampleAccessions = data.getSamplesDB();


            Set<String> differentGroup = new HashSet<>();

            List<SingleCheck> allGroupsChecks;

            if (testPerformance) {
                allGroupsChecks = groupAccessions.stream().limit(checkNumber).map(acc -> new SingleCheck(acc,
                        threadPool.submit(context.getBean(GroupXmlMatch.class, acc)))).collect(Collectors.toList());
            } else {
                allGroupsChecks = groupAccessions.stream().map(acc -> new SingleCheck(acc,
                        threadPool.submit(context.getBean(GroupXmlMatch.class, acc)))).collect(Collectors.toList());
            }

            for (SingleCheck groupCheck: allGroupsChecks) {
                try {
                    boolean match = groupCheck.getFuture().get();
                    if (!match) {
                       differentGroup.add(groupCheck.getAccession());
                    }
                } catch (InterruptedException e) {
                    log.error(String.format("Tasked interrupted for %s: ", groupCheck.getAccession()));
                } catch (ExecutionException e) {
                    log.error(String.format("Error while retrieving group %s XML: ",groupCheck.getAccession()));
                    if (e.getCause().getClass() == JDOMParseException.class) {
                        detailWriter.println(String.format("Parse error for %s: %s", groupCheck.getAccession(),e.getMessage()));
                    } else {
                        detailWriter.println(String.format("Exception for %s: %s", groupCheck.getAccession(), e.getMessage()));
                    }
                } catch (Exception e) {
                    log.error(String.format("Unexpected exception for group %s ", groupCheck.getAccession()),e );
                }

            }

            summaryWriter.println(String.format("Number of group compared:\t%d",allGroupsChecks.size()));
            summaryWriter.println(String.format("Non matching groups:\t%d",differentGroup.size()));
            if (!differentGroup.isEmpty()) {
                detailWriter.println("Group Accessions not matching legacy version:");
                differentGroup.forEach(detailWriter::println);
            } else {
                detailWriter.println("All groups match their legacy versions");
            }

            Set<String> differentSample = new HashSet<>();
            List<SingleCheck> allSamplesChecks;

            if (testPerformance) {
                allSamplesChecks = sampleAccessions.stream().limit(checkNumber).map(acc -> new SingleCheck(acc,
                        threadPool.submit(context.getBean(SampleXmlMatch.class, acc)))).collect(Collectors.toList());
            } else {
                allSamplesChecks = sampleAccessions.stream().map(acc -> new SingleCheck(acc,
                        threadPool.submit(context.getBean(SampleXmlMatch.class, acc)))).collect(Collectors.toList());
            }

            for (SingleCheck sampleCheck: allSamplesChecks) {
                try {
                    boolean match = sampleCheck.getFuture().get();
                    if (!match) {
                        differentSample.add(sampleCheck.getAccession());
                    }
                } catch (InterruptedException e) {
                    log.error(String.format("Tasked interrupted for %s: ", sampleCheck.getAccession()));
                } catch (ExecutionException e) {
                    log.error(String.format("Error while retrieving sample %s XML: ",sampleCheck.getAccession()));
                    if (e.getCause().getClass() == JDOMParseException.class) {
                        detailWriter.println(String.format("Parse error for %s: %s", sampleCheck.getAccession(),e.getMessage()));
                    } else {
                        detailWriter.println(String.format("Exception for %s: %s", sampleCheck.getAccession(), e.getMessage()));
                    }
                } catch (Exception e) {
                    log.error(String.format("Unexpected exception for sample %s ",sampleCheck.getAccession()),e );
                }

            }

            summaryWriter.println(String.format("Number of samples compared:\t%d",allSamplesChecks.size()));
            summaryWriter.println(String.format("Non matching samples:\t%d",differentSample.size()));
            if (!differentSample.isEmpty()) {
                detailWriter.println("Samples Accessions not matching legacy version:");
                differentSample.forEach(detailWriter::println);
            } else {
                detailWriter.println("All samples match their legacy version");
            }

            reportStatus = true;

        long endTime = System.nanoTime();
        summaryWriter.println(String.format("\n\n\nXML report duration: %dms",(endTime - startTime)/1000000));

        } catch (IOException e) {
            log.error("Error while creating report", e.getMessage());
        } finally {
            if (summaryWriter != null) { summaryWriter.flush(); summaryWriter.close(); }
            if (detailWriter != null) { detailWriter.flush(); detailWriter.close(); }

            try {
                threadPool.shutdown();
                threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("Error while interrupting thread pool in XML report: " + e.getMessage());
            }


        }

        return reportStatus;

    }




}
