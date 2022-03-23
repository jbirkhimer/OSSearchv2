package edu.si.ossearch.scheduler.service;

import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.entity.JobState;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;

import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
public interface JobService {

    boolean scheduleNewJob(CrawlSchedulerJobInfo jobInfo);

    boolean updateScheduleJob(CrawlSchedulerJobInfo jobInfo);

    boolean unScheduleJob(String jobName, String jobGroup);

    boolean deleteJob(String jobName, String jobGroup);


    boolean pauseJob(String jobName, String jobGroup);

    boolean resumeJob(String jobName, String jobGroup);

    boolean stopJob(String jobName, String jobGroup);

    boolean startJobNow(String jobName, String jobGroup);


    List<CrawlSchedulerJobInfo> getAllJobList();

    List<Map<String, Object>> getAllJobs();

    boolean isJobWithNamePresent(String jobName, String jobGroup);

    boolean isJobRunning(String jobName, String jobGroup);

    JobState getJobState(String jobName, String jobGroup);

    SchedulerMetaData getMetaData() throws SchedulerException;


}
