package edu.si.ossearch.scheduler.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.nutch.Crawler;
import edu.si.ossearch.scheduler.entity.CrawlLog;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.entity.CrawlStepLog;
import edu.si.ossearch.scheduler.repository.CrawlLogRepository;
import edu.si.ossearch.scheduler.repository.CrawlSchedulerJobInfoRepository;
import edu.si.ossearch.scheduler.repository.CrawlStepLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

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

        try {
            CrawlSchedulerJobInfo job = schedulerRepository.findByJobNameAndJobGroup(context.getJobDetail().getKey().getName(), context.getJobDetail().getKey().getGroup());
            crawler.config(jobId, context.getJobDetail().getKey());
            if (job.isReindex()) {
                crawler.reindex();
            } else if (job.isRecrawl()) {
                crawler.reCrawl();
            } else {
                crawler.crawl();
            }
            log.info("Job ** jobKey, {}, jobId: {} ** Finished................", jobId, context.getJobDetail().getKey());
        } catch (OSSearchException e) {
            log.error("Job ** jobKey, {}, jobId: {} ** FAILED !!!", jobId, context.getJobDetail().getKey(), e);
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
