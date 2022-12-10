package edu.si.ossearch.scheduler.job;

import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.nutch.Crawler;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.repository.CrawlLogRepository;
import edu.si.ossearch.scheduler.repository.CrawlSchedulerJobInfoRepository;
import edu.si.ossearch.scheduler.repository.CrawlStepLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo.JobType.*;

/**
 * @author jbirkhimer
 */
@Slf4j
@Component
@DisallowConcurrentExecution
//@PersistJobDataAfterExecution
public class CrawlCollectionJob implements InterruptableJob {

    @Autowired
    Crawler crawler;

    @Autowired
    private CrawlSchedulerJobInfoRepository schedulerRepository;

    @Autowired
    private CrawlLogRepository crawlLogRepository;

    @Autowired
    private CrawlStepLogRepository crawlStepLogRepository;

    protected AtomicBoolean stopFlag = new AtomicBoolean(false);
    protected AtomicBoolean errorFlag = new AtomicBoolean(false);

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * fires that is associated with the <code>Job</code>.
     * </p>
     *
     * <p>
     * The implementation may wish to set a
     * {@link JobExecutionContext#setResult(Object) result} object on the
     * {@link JobExecutionContext} before this method exits.  The result itself
     * is meaningless to Quartz, but may be informative to
     * <code>{@link JobListener}s</code> or
     * <code>{@link TriggerListener}s</code> that are watching the job's
     * execution.
     * </p>
     *
     * @param context
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        String jobId = jobDataMap.getString("collectionName")+"_"+new Date().getTime();

        jobDataMap.put("jobId", jobId);

        log.info("Job ** jobKey, {}, jobId: {} ** fired @ {}, jobDataMap: {}", jobId, context.getJobDetail().getKey(), context.getFireTime(), jobDataMap.getWrappedMap());

        CrawlSchedulerJobInfo job = schedulerRepository.findByJobNameAndJobGroup(context.getJobDetail().getKey().getName(), context.getJobDetail().getKey().getGroup());

        try {

            crawler.config(jobId, context.getJobDetail().getKey(), job.getJobType());

            if (job.isReindex() || job.getJobType() == REINDEX) {
                crawler.reindex();
                resetJob(job);
            } else if (job.isRecrawl() || job.getJobType() == RECRAWL) {
                crawler.reCrawl();
                resetJob(job);
                schedulerRepository.save(job);
            } else if (job.getJobType() == ADD_URLS) {
                crawler.crawl();
                schedulerRepository.delete(job);
            } else {
                crawler.crawl();
                resetJob(job);
            }

            log.info("Job ** jobKey, {}, jobId: {} ** Finished................", jobId, context.getJobDetail().getKey());
        } catch (Exception | OSSearchException e) {
            log.error("Job ** jobKey, {}, jobId: {} ** FAILED !!!", jobId, context.getJobDetail().getKey(), e);
            resetJob(job);
        }
    }

    private void resetJob(CrawlSchedulerJobInfo job) {
        if (job.getJobType() != ADD_URLS) {

            job.setJobType(SCHEDULED_CRAWL);
            job.setReindex(false);
            job.setRecrawl(false);

            schedulerRepository.save(job);
        }
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a user
     * interrupts the <code>Job</code>.
     * </p>
     *
     * @throws UnableToInterruptJobException if there is an exception while interrupting the job.
     */
    @Override
    public void interrupt() throws UnableToInterruptJobException {
        System.out.println("Stopping crawl... ");
        stopFlag.set(true);
        crawler.interrupt(stopFlag.get());
    }
}
