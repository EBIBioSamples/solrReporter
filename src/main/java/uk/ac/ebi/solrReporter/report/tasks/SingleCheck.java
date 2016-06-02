package uk.ac.ebi.solrReporter.report.tasks;

import java.util.concurrent.Future;

/**
 * Created by lucacherubin on 2016/05/26.
 */
public class SingleCheck {
    private String accession;

    private Future<Boolean> future;

    public SingleCheck(String accession, Future<Boolean> future) {
        this.accession = accession;
        this.future = future;
    }

    public Future<Boolean> getFuture() {
        return this.future;
    }

    public String getAccession() {
        return accession;
    }
}
