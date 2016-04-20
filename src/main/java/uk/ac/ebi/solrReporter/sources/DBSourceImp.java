package uk.ac.ebi.solrReporter.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class DBSourceImp implements DBSource {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<String> getSamplesAccessions() {
        String samplesAccQuery = "SELECT b.ACC FROM BIO_PRODUCT b " +
                "INNER JOIN MSI_SAMPLE ms ON b.ID = ms.SAMPLE_ID " +
                "INNER JOIN MSI msi ON msi.ID = ms.MSI_ID " +
                "WHERE msi.RELEASE_DATE < SYSDATE";

        List list = jdbcTemplate.queryForList(samplesAccQuery);
        System.out.println(list.get(0));
        System.out.println(list.get(1));

        return null;
    }

    @Override
    public List<String> getGroupsAccessions() {
        String groupsAccQuery = "SELECT gp.ACC FROM BIO_SMP_GRP gp " +
                "INNER JOIN MSI_SAMPLE_GROUP mg ON gp.ID = mg.GROUP_ID " +
                "INNER JOIN MSI msi ON mg.MSI_ID = msi.ID " +
                "WHERE msi.RELEASE_DATE < SYSDATE";

        List list = jdbcTemplate.queryForList(groupsAccQuery);
        System.out.println(list.get(0));
        System.out.println(list.get(1));

        return null;
    }

}
