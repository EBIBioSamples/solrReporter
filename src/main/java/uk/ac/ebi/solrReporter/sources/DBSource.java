package uk.ac.ebi.solrReporter.sources;

import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Service
public class DBSource implements Source {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;

    private String sourceURL;

    @Autowired
    public void setDataSource(OracleDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        try {
            sourceURL = dataSource.getURL();
        } catch (SQLException e) {
            log.error("Error getting DB URL.", e);
        }
    }

    @Override
    public Set<String> getSamplesAccessions() {
        log.info("Getting public samples accessions from DB.");

        String samplesAccQuery =
                "WITH res AS " +
                "( " +
                "SELECT b.ACC, COUNT(msi.ACC) AMOUNT FROM BIO_PRODUCT b, MSI_SAMPLE ms, MSI msi " +
                "WHERE b.ID = ms.SAMPLE_ID " +
                "AND msi.ID = ms.MSI_ID " +
                "AND msi.RELEASE_DATE < SYSDATE + 1 " +
                "AND( b.PUBLIC_FLAG IS NULL OR b.PUBLIC_FLAG = 1) " +
                "GROUP BY b.ACC" +
                ") " +
                "SELECT res.ACC FROM res " +
                "WHERE res.AMOUNT = 1";

        Set<String> accessions = new HashSet<>();
        jdbcTemplate.queryForList(samplesAccQuery).forEach(row -> accessions.add((String) row.get("ACC")));

        log.info("Successfully fetched " + accessions.size() + " samples accessions from DB.");
        return accessions;
    }

    @Override
    public Set<String> getGroupsAccessions() {
        log.info("Getting public groups accessions from DB.");

        String groupsAccQuery =
                "WITH res AS" +
                "(" +
                "SELECT gp.ACC, COUNT(msi.ACC) AMOUNT FROM BIO_SMP_GRP gp, MSI_SAMPLE_GROUP mg, MSI msi " +
                "WHERE gp.ID = mg.GROUP_ID " +
                "AND mg.MSI_ID = msi.ID " +
                "AND msi.RELEASE_DATE < SYSDATE + 1 " +
                "AND (gp.PUBLIC_FLAG IS NULL OR gp.PUBLIC_FLAG = 1) " +
                "GROUP BY gp.ACC " +
                ") " +
                "SELECT res.ACC FROM res " +
                "WHERE res.AMOUNT = 1";

        Set<String> accessions = new HashSet<>();
        jdbcTemplate.queryForList(groupsAccQuery).forEach(row -> accessions.add((String) row.get("ACC")));

        log.info("Successfully fetched " + accessions.size() + " groups accessions from DB.");
        return accessions;
    }

    public String getSourceUrl() {
        return sourceURL;
    }

}
