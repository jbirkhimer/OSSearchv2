package edu.si.ossearch.scheduler.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo.JobType;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.Map;

/**
 * @author jbirkhimer
 */
@Projection(name = "crawlSchedulerJobInfoInfo", types = { CrawlSchedulerJobInfo.class })
public interface CrawlSchedulerJobInfoInfo {
    Long getId();

//    String getJobId();

    String getJobName();

    String getJobGroup();

    String getJobStatus();

    String getJobClass();

    String getDescription();

    boolean isCronJob();

    String getCronExpression();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getStartTime();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getNextFireTime();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getPreviousFireTime();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getFinalFireTime();

    String getCollectionName();

    String getCollectionId();

    int getNumberOfRounds();

    boolean isReindex();

    boolean isRecrawl();

    JobType getJobType();

    Map<String, String> getCrawlOptions();

    Map<String, String> getNutchProperties();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getUpdatedDate();

    NutchStepArgsInfo getNutchStepArgs();

    interface NutchStepArgsInfo {
        Long getId();

        Map<String, String> getInject();

        Map<String, String> getSitemap();

        Map<String, String> getGenerate();

        Map<String, String> getParse();

        Map<String, String> getUpdatedb();

        Map<String, String> getUpdatehostdb();

        Map<String, String> getInvertlinks();

        Map<String, String> getDedup();

        Map<String, String> getFetch();

        Map<String, String> getIndex();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getCreatedDate();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getUpdatedDate();
    }
}
