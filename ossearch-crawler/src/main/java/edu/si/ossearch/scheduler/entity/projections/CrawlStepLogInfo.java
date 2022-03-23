package edu.si.ossearch.scheduler.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.scheduler.entity.CrawlLog;
import edu.si.ossearch.scheduler.entity.CrawlStepLog;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

/**
 * @author jbirkhimer
 */
@Projection(name = "crawlStepLogInfo", types = { CrawlStepLog.class })
public interface CrawlStepLogInfo {
    Long getId();

    String getJobKey();

    String getJobId();

    CrawlLog.StepType getStepType();

    CrawlLog.State getState();

    int getRound();

    String getArgs();

    String getDbStats();

    long getSolrCount();

    String getErrors();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getUpdatedDate();
}
