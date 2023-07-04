package edu.si.ossearch.utils.backup_restore.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.Map;

/**
 * Projection for {@link edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo}
 */
@Projection(name = "collectionExport", types = { CrawlSchedulerJobInfo.class })
public interface CrawlSchedulerJobInfoInfoExport {
    //Long getId();

    String getJobName();

    String getJobGroup();

    String getJobStatus();

    String getJobClass();

    String getDescription();

    boolean getCronJob();

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

    //String getCollectionId();

    int getNumberOfRounds();

    boolean getReindex();

    boolean getRecrawl();

    CrawlSchedulerJobInfo.JobType getJobType();

    Map<String, String> getCrawlOptions();

    Map<String, String> getNutchProperties();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getUpdatedDate();

    NutchStepArgsInfo getNutchStepArgs();

    /**
     * Projection for {@link edu.si.ossearch.scheduler.entity.NutchStepArgs}
     */
    interface NutchStepArgsInfo {
        //Long getId();

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

        Map<String, String> getSegmentMerger();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getCreatedDate();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getUpdatedDate();
    }
}