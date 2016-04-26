package uk.ac.ebi;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompareListsTest {

    private java.util.List<String> dbAcc, solrAcc;

    public void setSolrAcc() {
        solrAcc = new ArrayList<>(Arrays.asList("SAMEA387513", "SAMN03841453", "SAMEA3476745", "SAMN03002262",
                "SAMN02918779", "SAMN03277393", "SAMEA388175", "SAMEA1126715", "SAMEA719254", "SAME1348851", "SAME134885"));
    }

    public void setDbAcc() {
        dbAcc = new ArrayList<>(Arrays.asList("SAMEA387513", "SAMN03841453", "SAMEA3476745", "SAMN03002262",
                "SAMN02918779", "SAMN03277393", "SAMEA388175", "SAMEA1126715", "SAMEA719254", "SAME1348851"));
    }

    @Test
    public void test() throws Exception {
        setDbAcc();
        setSolrAcc();

        // Generates list of accessions missing in the solr index
        List<String> missinInIndex = dbAcc.stream().filter(db -> !solrAcc.contains(db)).collect(Collectors.toList());
        Assert.assertEquals("[]", missinInIndex.toString());


        // Generates list of accessions not supposed to be in the solr index
        List<String> presentInIndex = solrAcc.stream().filter(solr -> !dbAcc.contains(solr)).collect(Collectors.toList());
        Assert.assertEquals("[SAME134885]", presentInIndex.toString());
    }
}
