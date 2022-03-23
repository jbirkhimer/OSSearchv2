package edu.si.ossearch.scheduler.service;

import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.quartz.*;
import org.springframework.stereotype.Component;

/**
 * @author jbirkhimer
 */
@Slf4j
@Component
public class JobUtil {

    /**
     * Conveys the detail properties of a given Job instance
     *
     * @param jobClass              job class are you storing details for
     * @param isDurable             Job needs to be persisted even after completion. if true, job will be persisted, not otherwise.
     * @param crawlSchedulerJobInfo information about the timer
     * @return JobDetail object
     */
    public JobDetail createJob(final Class jobClass, boolean isDurable, final CrawlSchedulerJobInfo crawlSchedulerJobInfo) {
        //Map that has all the data we need for our jobs and will be stored with the timer
        final JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put("jobId", crawlSchedulerJobInfo.getJobId());
        jobDataMap.put("collectionId", crawlSchedulerJobInfo.getCollectionId());
        jobDataMap.put("collectionName", crawlSchedulerJobInfo.getCollectionName());
//        jobDataMap.put("numberOfRounds", crawlSchedulerJobInfo.getNumberOfRounds());
//        jobDataMap.put("index", crawlSchedulerJobInfo.isRunIndexing());
//        jobDataMap.putAll(crawlSchedulerJobInfo.getNutchProperties());
//        jobDataMap.put("crawlOptions", new JSONObject(crawlSchedulerJobInfo.getCrawlOptions()).toString());
//        jobDataMap.put("nutchStepArgs", new JSONObject(crawlSchedulerJobInfo.getNutchStepArgs()).toString());
//        jobDataMap.put("nutchProperties", new JSONObject(crawlSchedulerJobInfo.getNutchProperties()).toString());

        //Build the new job
        return JobBuilder
                .newJob(jobClass)
                .storeDurably(isDurable)
                .withIdentity(crawlSchedulerJobInfo.getJobName(), crawlSchedulerJobInfo.getJobGroup()) //use something unique
                .withDescription(crawlSchedulerJobInfo.getDescription())
                .setJobData(jobDataMap)
                .build();
    }

    /**
     * Create cron trigger.
     *
     * @param crawlSchedulerJobInfo information about the timer
     *
     * @return Trigger
     */
    public Trigger createCronTrigger(final CrawlSchedulerJobInfo crawlSchedulerJobInfo) {
        //Build the schedule for when and how often we want the job to run
        CronScheduleBuilder builder = CronScheduleBuilder
                .cronSchedule(crawlSchedulerJobInfo.getCronExpression())
                .withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder
                .newTrigger()
                .withIdentity(crawlSchedulerJobInfo.getJobName(), crawlSchedulerJobInfo.getJobGroup())
                .withSchedule(builder)
                .startNow()
                .build();
    }

    /**
     * Create a Single trigger.
     *
     * @param crawlSchedulerJobInfo information about the timer
     *
     * @return Trigger
     */
    public SimpleTrigger createSimpleTrigger(final CrawlSchedulerJobInfo crawlSchedulerJobInfo) {

        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0);

        TriggerBuilder<SimpleTrigger> simpleTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity(crawlSchedulerJobInfo.getJobName(), crawlSchedulerJobInfo.getJobGroup())
                .withSchedule(builder);

        if (crawlSchedulerJobInfo.getStartTime() != null) {
            return simpleTrigger
                    .startAt(crawlSchedulerJobInfo.getStartTime())
                    .build();
        } else {
            return simpleTrigger
                    .startNow()
                    .build();
        }
    }

}
