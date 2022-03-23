package edu.si.ossearch.scheduler.service.impl;

import edu.si.ossearch.dao.entity.CrawlConfig;
import edu.si.ossearch.dao.repository.CrawlConfigRepository;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.entity.JobState;
import edu.si.ossearch.scheduler.job.CrawlCollectionJob;
import edu.si.ossearch.scheduler.repository.CrawlSchedulerJobInfoRepository;
import edu.si.ossearch.scheduler.service.JobService;
import edu.si.ossearch.scheduler.service.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.quartz.Trigger.TriggerState.PAUSED;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private CrawlSchedulerJobInfoRepository schedulerRepository;

    @Autowired
    private JobUtil jobUtil;


//    @PostConstruct
//    public void init() {
//        try {
//            scheduler.start();
////            scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this));
//        } catch (SchedulerException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    @PreDestroy
//    public void preDestroy() {
//        try {
//            scheduler.shutdown();
//        } catch (SchedulerException e) {
//            log.error(e.getMessage(), e);
//        }
//    }

    @Override
    public boolean scheduleNewJob(CrawlSchedulerJobInfo jobInfo) {

        JobDetail jobDetail = null;
        if (jobInfo.getJobGroup().equals("addUrls")) {
            jobDetail = jobUtil.createJob(CrawlCollectionJob.class, false, jobInfo);
        } else {
            jobDetail = jobUtil.createJob(CrawlCollectionJob.class, true, jobInfo);
        }

        Trigger trigger;

        if (jobInfo.isCronJob()) {
            trigger = jobUtil.createCronTrigger(jobInfo);
            log.info("creating cron trigger for key: {} at date: {}", jobDetail.getKey(), trigger.getStartTime());
        } else {
            trigger = jobUtil.createSimpleTrigger(jobInfo);
            log.info("creating one time trigger for key: {} at date: {}", jobDetail.getKey(), trigger.getStartTime());
        }

        try {
            Date dt = scheduler.scheduleJob(jobDetail, trigger);
            jobInfo.setJobStatus("SCHEDULED");
            jobInfo.setStartTime(trigger.getStartTime());
            jobInfo.setNextFireTime(trigger.getNextFireTime());
            jobInfo.setPreviousFireTime(trigger.getPreviousFireTime());
            jobInfo.setFinalFireTime(trigger.getFinalFireTime());
            schedulerRepository.saveAndFlush(jobInfo);
            log.info("Job with key jobKey: {} and group: {} scheduled successfully for date: {}", jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), dt);
            return true;
        } catch (SchedulerException e) {
            log.error("SchedulerException while scheduling job with key: {} message: {}", jobDetail.getKey(), e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return false;
    }

    @Override
    public boolean updateScheduleJob(CrawlSchedulerJobInfo jobInfo) {

        JobDetail jobDetail = jobUtil.createJob(CrawlCollectionJob.class, true, jobInfo);

        Trigger newTrigger;

        if (jobInfo.isCronJob()) {
            newTrigger = jobUtil.createCronTrigger(jobInfo);
            log.info("rescheduled cron newTrigger for key: {} at date: {}", jobDetail.getKey(), newTrigger.getStartTime());
        } else {
            newTrigger = jobUtil.createSimpleTrigger(jobInfo);
            log.info("rescheduled one time newTrigger for key: {} at date: {}", jobDetail.getKey(), newTrigger.getStartTime());
        }

        try {
            //Check if job is paused so that we can keep it paused after updating schedule
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            boolean paused = false;
            for (Trigger trigger : triggers) {
                if (scheduler.getTriggerState(trigger.getKey()) == PAUSED) {
                    paused = true;
                    break;
                }
            }

            // Reschedule the Job
            Date dt = scheduler.rescheduleJob(newTrigger.getKey(), newTrigger);
            jobInfo.setJobStatus("EDITED & SCHEDULED");
            jobInfo.setStartTime(newTrigger.getStartTime());
            jobInfo.setNextFireTime(newTrigger.getNextFireTime());
            jobInfo.setPreviousFireTime(newTrigger.getPreviousFireTime());
            jobInfo.setFinalFireTime(newTrigger.getFinalFireTime());

            // If the Job was paused before rescheduling set it as paused again
            if (paused) {
                scheduler.pauseJob(jobDetail.getKey());
                jobInfo.setJobStatus("EDITED & PAUSED");
            }

            schedulerRepository.save(jobInfo);
            log.info("Trigger associated with jobKey: {}, rescheduled successfully for date: {}", jobDetail.getKey(), dt);
            return true;
        } catch (Exception e) {
            log.error("SchedulerException while scheduling job with key: {} message: {}", jobDetail.getKey(), e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return false;
    }

    @Override
    public boolean unScheduleJob(String jobName, String jobGroup) {

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        TriggerKey triggerKey = new TriggerKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        log.info("Parameters received for un-Scheduling job : jobKey: {}", triggerKey);
        try {
            boolean status = scheduler.unscheduleJob(triggerKey);
            jobInfo.setJobStatus("EDITED & UNSCHEDULED");
            jobInfo.setStartTime(null);
            jobInfo.setNextFireTime(null);
            jobInfo.setPreviousFireTime(null);
            jobInfo.setFinalFireTime(null);
            schedulerRepository.save(jobInfo);
            log.info("Trigger associated with jobKey: {}, unscheduled with status: {}", triggerKey, status);
            return status;
        } catch (SchedulerException e) {
            log.error("SchedulerException while un-Scheduling job with key: {}, message: {}",triggerKey, e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public boolean deleteJob(String jobName, String jobGroup) {

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        log.info("Parameters received for deleting job with jobKey: {}", jobKey);

        try {
            boolean status = scheduler.deleteJob(jobKey);
            schedulerRepository.delete(jobInfo);
            log.info("Job with jobKey: {}, deleted with status: {}",jobKey, status);
            return status;
        } catch (SchedulerException e) {
            log.error("SchedulerException while deleting job with key: {}, message: {}",jobKey, e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public boolean pauseJob(String jobName, String jobGroup) {

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        log.info("Parameters received for pausing job with jobKey: {},", jobKey);

        try {
            scheduler.pauseJob(jobKey);
            jobInfo.setJobStatus("PAUSED");
            schedulerRepository.save(jobInfo);
            log.info("Job with jobKey: {} paused successfully.", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("SchedulerException while pausing job with key: {} message: {}", jobKey , e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public boolean resumeJob(String jobName, String jobGroup) {

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        log.info("Parameters received for resuming job - jobKey :"+jobKey);

        try {
            scheduler.resumeJob(jobKey);
            jobInfo.setJobStatus("RESUMED");
            schedulerRepository.save(jobInfo);
            log.info("Job with jobKey: {}, resumed successfully.", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("SchedulerException while resuming job with key: {} message: {}", jobKey , e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public boolean stopJob(String jobName, String jobGroup) {
        log.info("JobServiceImpl.stopJob()");

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        log.info("Parameters received for stopping job - jobKey :" + jobKey);

        try{
            boolean status = scheduler.interrupt(jobKey);
            jobInfo.setJobStatus("STOPPED & SCHEDULED");
            schedulerRepository.save(jobInfo);
            log.info("Job with jobKey: {}, stopped with status: {}", jobKey, status);
            return status;
        } catch (SchedulerException e) {
            log.error("SchedulerException while stopping job. error message: {}", e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public boolean startJobNow(String jobName, String jobGroup) {
        log.info("JobServiceImpl.stopJob()");

        CrawlSchedulerJobInfo jobInfo = schedulerRepository.findByJobNameAndJobGroup(jobName, jobGroup);

        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        log.info("Parameters received for starting job now - jobKey: {}", jobKey);

        try {
            scheduler.triggerJob(jobKey);
            jobInfo.setJobStatus("SCHEDULED & STARTED");
            schedulerRepository.save(jobInfo);
            log.info("Job with jobKey: {}, started now successfully.", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("SchedulerException while starting job with key: {} message: {} ", jobKey , e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }



    @Override
    public boolean isJobWithNamePresent(String jobName, String jobGroup) {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroup);

            if (scheduler.checkExists(jobKey)) {
                return true;
            }

        } catch (SchedulerException e) {
            log.error("SchedulerException while checking job with name and group exist: {}", e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public boolean isJobRunning(String jobName, String jobGroup) {

        log.info("Parameters received for checking job is running ? - jobKey: {}, jobGroup: {}", jobName, jobGroup);
        try {

            List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    String jobNameDB = jobCtx.getJobDetail().getKey().getName();
                    String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
                    if (jobName.equalsIgnoreCase(jobNameDB) && jobGroup.equalsIgnoreCase(groupNameDB)) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("SchedulerException while checking job with jobKey: {}, jobGroup: {}, is running. error message: {}", jobName, jobGroup, e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public JobState getJobState(String jobName, String jobGroup) {
        log.info("JobServiceImpl.getJobState()");

        try {
            JobKey jobKey = new JobKey(jobName, jobGroup);

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            if (triggers != null && triggers.size() > 0) {
                for (Trigger trigger : triggers) {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    log.info("trigger state: {}", triggerState);
                    return JobState.toJobState(triggerState);
                }
            }
        } catch (SchedulerException e) {
            log.error("SchedulerException while checking job with name and group exist: {}", e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return JobState.NONE;
    }

    @Override
    public List<CrawlSchedulerJobInfo> getAllJobList() {
        return schedulerRepository.findAll();
    }

    @Override
    public List<Map<String, Object>> getAllJobs() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {

            for (String groupName : scheduler.getJobGroupNames()) {

                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();

                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobName);
                    map.put("groupName", jobGroup);

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

                    String startTime = "None";
                    String nextFireTime = "None";
                    String lastFiredTime = "None";
                    String finalFiredTime = "None";
                    String cronExpression = "None";

                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh:mm a");

                    if (triggers.size() > 0) {
                        startTime = triggers.get(0).getStartTime() !=null ? df.format(triggers.get(0).getStartTime()) : "None";
                        nextFireTime = triggers.get(0).getNextFireTime() != null ? df.format(triggers.get(0).getNextFireTime()) : "None";
                        lastFiredTime = triggers.get(0).getPreviousFireTime() != null ? df.format(triggers.get(0).getPreviousFireTime()) : "None";
                        finalFiredTime = triggers.get(0).getFinalFireTime() != null ? df.format(triggers.get(0).getFinalFireTime()) : "None";

                        if (triggers.get(0) instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
                            cronExpression = cronTrigger.getCronExpression();
                        }
                    }

                    map.put("startTime", startTime);
                    map.put("lastFiredTime", lastFiredTime);
                    map.put("nextFireTime", nextFireTime);
                    map.put("finalFiredTime", finalFiredTime);
                    map.put("cronExpression", cronExpression);

                    if (this.isJobRunning(jobName, jobGroup)) {
                        map.put("jobStatus", "RUNNING");
                    } else {
                        JobState jobState = this.getJobState(jobName, jobGroup);
                        map.put("jobStatus", jobState.toString());
                    }

                    list.add(map);
                }

            }
        } catch (SchedulerException e) {
            log.error("SchedulerException while fetching all jobs. error message: {}", e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return list;
    }

    @Override
    public SchedulerMetaData getMetaData() throws SchedulerException {
        return scheduler.getMetaData();
    }

}
