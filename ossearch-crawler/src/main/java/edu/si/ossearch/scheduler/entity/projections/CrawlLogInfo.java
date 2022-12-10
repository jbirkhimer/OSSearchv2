package edu.si.ossearch.scheduler.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.scheduler.entity.CrawlLog;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo.JobType;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

/**
 * @author jbirkhimer
 */
@Projection(name = "crawlLogInfo", types = { CrawlLog.class })
public interface CrawlLogInfo {
    Long getId();

    String getJobKey();

    String getJobId();

    JobType getJobType();

    String getJobConfig();

    CrawlLog.State getState();

    int getRounds();

    int getCurrentRound();

    CrawlLog.StepType getCurrentStep();

    String getDbStats();

    long getSolrCount();

    String getErrors();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getUpdatedDate();
}
