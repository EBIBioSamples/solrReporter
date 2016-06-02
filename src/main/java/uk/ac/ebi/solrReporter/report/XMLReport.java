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
public class XMLReport {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DecimalFormat myFormat = new DecimalFormat("###,###.###");
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private SolrSource solrSource;

    @Autowired
    private XmlApiService xmlApiService;

    @Autowired
    private ApplicationContext context;

    @Value("${threadPoolCount:4}")
    private int threadPoolCount;

    @Value("${filePath:./}")
    private String path;

    @Value("${testPerformance:false}")
    private boolean testPerformance;

    private ExecutorService threadPool = null;
    public boolean generateReport(ReportData data) {

        final int checkNumber = threadPoolCount * 125; // maintain constant the ratio between number of checked samples and threads
        boolean reportStatus = false;

        Calendar cal = Calendar.getInstance();
        String currentDate = dateFormat.format(cal.getTime());

        File fileSummary = new File(path + "xml_report_" + dateFormat.format(cal.getTime()) + ".txt");
        File fileDetails = new File(path + "xml_report_detail_" + dateFormat.format(cal.getTime()) + ".txt");
        File fileParseErrors = new File(path + "xml_parse_exception_" + dateFormat.format(cal.getTime()) + ".txt" );
        File fileAllErrors = new File(path + "xml_all_exceptions_" + dateFormat.format(cal.getTime()) + ".txt");

        threadPool = Executors.newFixedThreadPool(threadPoolCount);
        PrintWriter summary_writer = null;
        PrintWriter detail_writer = null;
        PrintWriter parse_exception_writer = null;
        PrintWriter all_exceptions_writer = null;

        long startTime = System.nanoTime();


        try  {

            summary_writer = new PrintWriter(fileSummary);
            detail_writer = new PrintWriter(fileDetails);
            parse_exception_writer = new PrintWriter(fileParseErrors);
            all_exceptions_writer = new PrintWriter(fileAllErrors);

            summary_writer.println("\n-- XML API Summary --\n");
            detail_writer.println("-- XML API Details --\n");

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
                    all_exceptions_writer.println(String.format("Exception for %s: %s", groupCheck.getAccession(), e.getMessage()));
                    if (e.getCause().getClass() == JDOMParseException.class) {
                        parse_exception_writer.println(String.format("Parse error for %s: %s", groupCheck.getAccession(),e.getMessage()));
                    }
                } catch (Exception e) {
                    log.error(String.format("Unexpected exception for group %s ", groupCheck.getAccession()),e );
                }

            }

            summary_writer.println(String.format("Number of group compared:\t%d",allGroupsChecks.size()));
            summary_writer.println(String.format("Non matching groups:\t%d",differentGroup.size()));
            if (!differentGroup.isEmpty()) {
                detail_writer.println("Group Accessions not matching legacy version:");
                differentGroup.forEach(detail_writer::println);
            } else {
                detail_writer.println("All groups match their legacy versions");
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
                    all_exceptions_writer.println(String.format("Exception for %s: %s", sampleCheck.getAccession(), e.getMessage()));
                    if (e.getCause().getClass() == JDOMParseException.class) {
                        parse_exception_writer.println(String.format("Parse error for %s: %s", sampleCheck.getAccession(),e.getMessage()));
                    }
                } catch (Exception e) {
                    log.error(String.format("Unexpected exception for sample %s ",sampleCheck.getAccession()),e );
                }

            }

            summary_writer.println(String.format("Number of samples compared:\t%d",allSamplesChecks.size()));
            summary_writer.println(String.format("Non matching samples:\t%d",differentSample.size()));
            if (!differentSample.isEmpty()) {
                detail_writer.println("Samples Accessions not matching legacy version:");
                differentSample.forEach(detail_writer::println);
            } else {
                detail_writer.println("All samples match their legacy version");
            }

            reportStatus = true;

        long endTime = System.nanoTime();
        summary_writer.println(String.format("\n\n\nXML report duration: %dms",(endTime - startTime)/1000000));

        } catch (FileNotFoundException e) {
            log.error("Error while creating report", e.getMessage());
        } finally {
            if (summary_writer != null) { summary_writer.flush(); summary_writer.close(); }
            if (detail_writer != null) { detail_writer.flush(); detail_writer.close(); }
            if (parse_exception_writer != null) { parse_exception_writer.flush(); parse_exception_writer.close(); }
            if (all_exceptions_writer != null) { all_exceptions_writer.flush(); all_exceptions_writer.close(); }

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
