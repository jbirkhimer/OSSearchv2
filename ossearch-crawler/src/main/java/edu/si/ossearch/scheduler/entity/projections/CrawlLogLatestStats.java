package edu.si.ossearch.scheduler.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.scheduler.entity.CrawlLog;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo.JobType;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

/**
 * @author jbirkhimer
 */
@Projection(name = "crawlLogLatestStats", types = { CrawlLog.class })
public interface CrawlLogLatestStats {
    Long getId();

    String getJobKey();

    String getJobId();

    JobType getJobType();

    CrawlLog.State getState();

    int getRounds();

    String getDbStats();

    long getSolrCount();

    String getErrors();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getUpdatedDate();
}
