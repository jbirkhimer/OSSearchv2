package edu.si.ossearch.scheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

/**
 * Initializes SQL views that bridge the naming gap between Quartz and MySQL.
 *
 * <p>This class addresses a compatibility issue between Quartz Scheduler 2.3.2
 * and MySQL configurations using lower_case_table_names=1. Quartz hardcodes table
 * names in uppercase (e.g., "QRTZ_LOCKS") while MySQL with this setting forces
 * tables to lowercase.</p>
 *
 * <p>Solution: Creates views with uppercase names that map directly to the lowercase
 * tables. This allows Quartz to interact with its expected uppercase naming scheme
 * while the actual data remains in the lowercase tables.</p>
 *
 * <p>The views support all Quartz operations (read, insert, update, delete)
 * transparently, maintaining full functionality without modifying Quartz itself.</p>
 *
 * <p>These views are created automatically on application startup.</p>
 *
 * @see org.quartz.impl.jdbcjobstore.JobStoreTX
 * @see org.quartz.impl.jdbcjobstore.StdJDBCDelegate
 *
 * @author jbirkhimer
 */
@Configuration
public class QuartzViewsInitializer {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void createUppercaseViews() {
        String[] tables = {
                "LOCKS", "JOB_DETAILS", "TRIGGERS", "CALENDARS",
                "CRON_TRIGGERS", "FIRED_TRIGGERS", "PAUSED_TRIGGER_GRPS",
                "SCHEDULER_STATE", "SIMPLE_TRIGGERS", "SIMPROP_TRIGGERS",
                "BLOB_TRIGGERS"
        };

        for (String table : tables) {
            jdbcTemplate.execute(
                    "CREATE OR REPLACE VIEW si_search_db_dmz.QRTZ_" + table +
                            " AS SELECT * FROM si_search_db_dmz.qrtz_" + table.toLowerCase()
            );
        }
    }
}