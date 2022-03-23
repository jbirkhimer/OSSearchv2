package edu.si.ossearch.scheduler.controller;

import edu.si.ossearch.dao.entity.Collection;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.entity.JobState;
import edu.si.ossearch.scheduler.entity.ServerResponse;
import edu.si.ossearch.scheduler.service.JobService;
import edu.si.ossearch.scheduler.util.ServerResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
@Slf4j
@RestController
@RequestMapping("/api/scheduler")
//@Api(value = "Crawl Scheduler", tags = "Crawl Scheduler")
@Tag(description = "Crawl Scheduler", name = "Crawl Scheduler")
@SecurityRequirement(name = "bearerAuth")
public class CrawlSchedulerController {

    @Autowired
    private JobService jobService;

    public static final String DEFAULT_JOB_GROUP_NAME = "scheduled_crawl";

    @Operation(summary = "create crawl schedule", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "schedule")
    public ResponseEntity<ServerResponse> schedule(@RequestBody CrawlSchedulerJobInfo job) {

        log.info("CrawlSchedulerJobInfo: {}", job);

        String jobName = job.getJobName();

        if (job.getJobGroup() == null || job.getJobGroup().isEmpty()) {
            job.setJobGroup(DEFAULT_JOB_GROUP_NAME);
        }

        job.setCronJob(job.getCronExpression() != null && !job.getCronExpression().isEmpty());

        String jobGroup = job.getJobGroup();

        //Job Name is mandatory
        if (jobName == null || jobName.trim().equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, "Missing Job name", false));
        }

        //Check if job Name is unique;
        if (!jobService.isJobWithNamePresent(jobName,jobGroup)) {

            boolean status = jobService.scheduleNewJob(job);

            if (status) {
                return ResponseEntity.status(HttpStatus.CREATED).body(getServerResponse(ServerResponseCode.CREATED, jobService.getAllJobs()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getServerResponse(ServerResponseCode.JOB_WITH_SAME_NAME_EXIST,  "Job name already exists", false));
        }
    }

    @PutMapping(value = "update")
    public ResponseEntity<ServerResponse> update(@RequestBody CrawlSchedulerJobInfo job) {
        log.info("JobController.updateJob()");

        String jobName = job.getJobName();

        if (job.getJobGroup() == null || job.getJobGroup().isEmpty()) {
            job.setJobGroup(DEFAULT_JOB_GROUP_NAME);
        }

        job.setCronJob(job.getCronExpression() != null && !job.getCronExpression().isEmpty());

        String jobGroup = job.getJobGroup();

        //Job Name is mandatory
        if (jobName == null || jobName.trim().equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, "Missing Job name", false));
        }

        //Edit Job
        if (jobService.isJobWithNamePresent(jobName, jobGroup)) {

            boolean status = jobService.updateScheduleJob(job);
            if (status) {
                return ResponseEntity.status(HttpStatus.CREATED).body(getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, "Job with name: " + jobName + " and group: " + jobGroup + " does not exist", false));
        }
    }

    @PatchMapping("un-schedule")
    public ResponseEntity<ServerResponse> unschedule(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.unschedule()");

        boolean status = jobService.unScheduleJob(jobName, jobGroup);
        if (status) {
            return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, jobService.getAllJobs()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<ServerResponse> delete(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.delete()");

        if (jobService.isJobWithNamePresent(jobName, jobGroup)) {
            boolean isJobRunning = jobService.isJobRunning(jobName, jobGroup);

            if (!isJobRunning) {
                boolean status = jobService.deleteJob(jobName, jobGroup);
                if (status) {
                    return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, true));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
                }
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false));
            }
        } else {
            //Job doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, "Job with name: " + jobName + " and group: " + jobGroup + " does not exist", false));
        }
    }

    @PatchMapping("pause")
    public ResponseEntity<ServerResponse> pause(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.pause()");

        if (jobService.isJobWithNamePresent(jobName, jobGroup)) {

            boolean isJobRunning = jobService.isJobRunning(jobName, jobGroup);

            if (!isJobRunning) {
                boolean status = jobService.pauseJob(jobName, jobGroup);
                if (status) {
                    return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, true));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
                }
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false));
            }

        } else {
            //Job doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, "Job with name: " + jobName + " and group: " + jobGroup + " does not exist", false));
        }
    }

    @PatchMapping("resume")
    public ResponseEntity<ServerResponse> resume(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.resume()");

        if (jobService.isJobWithNamePresent(jobName, jobGroup)) {
            JobState jobState = jobService.getJobState(jobName, jobGroup);

            if (jobState == JobState.PAUSED) {
                log.info("Job current state is PAUSED, Resuming job...");
                boolean status = jobService.resumeJob(jobName, jobGroup);

                if (status) {
                    return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, true));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
                }
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(getServerResponse(ServerResponseCode.JOB_NOT_IN_PAUSED_STATE, false));
            }

        } else {
            //Job doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, "Job with name: " + jobName + " and group: " + jobGroup + " does not exist", false));
        }
    }

    @PatchMapping("stop")
    public ResponseEntity<ServerResponse> stopJob(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.stopJob()");

        if (jobService.isJobWithNamePresent(jobName, jobGroup)) {

            if (jobService.isJobRunning(jobName, jobGroup)) {
                boolean status = jobService.stopJob(jobName, jobGroup);
                if (status) {
                    return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, true));
                } else {
                    //Server error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
                }

            } else {
                //Job not in running state
                return ResponseEntity.status(HttpStatus.CONFLICT).body(getServerResponse(ServerResponseCode.JOB_NOT_IN_RUNNING_STATE, false));
            }

        } else {
            //Job doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, "Job with name: " + jobName + " and group: " + jobGroup + " does not exist", false));
        }
    }

    @PatchMapping("start")
    public ResponseEntity<ServerResponse> startJobNow(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.startJobNow()");

        if (jobService.isJobWithNamePresent(jobName, jobGroup)) {

            if (!jobService.isJobRunning(jobName, jobGroup)) {
                boolean status = jobService.startJobNow(jobName, jobGroup);

                if (status) {
                    //Success
                    return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, true));

                } else {
                    //Server error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
                }

            } else {
                //Job already running
                return ResponseEntity.status(HttpStatus.CONFLICT).body(getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false));
            }

        } else {
            //Job doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, "Job with name: " + jobName + " and group: " + jobGroup + " does not exist", false));
        }
    }

    @GetMapping("checkJobName")
    public ResponseEntity<ServerResponse> checkJobName(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.checkJobName()");

        //Job Name is mandatory
        if (jobName == null || jobName.trim().equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, "Missing Job name", false));
        }

        boolean status = jobService.isJobWithNamePresent(jobName, jobGroup);
        if (status) {
            return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, status));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
        }
    }

    @GetMapping("isJobRunning")
    public ResponseEntity<ServerResponse> isJobRunning(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.isJobRunning()");

        boolean status = jobService.isJobRunning(jobName, jobGroup);
        if (status) {
            return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, status));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getServerResponse(ServerResponseCode.ERROR, false));
        }
    }

    @GetMapping("jobState")
    public ResponseEntity<ServerResponse> getJobState(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup) {
        log.info("JobController.getJobState()");

        JobState jobState = jobService.getJobState(jobName, jobGroup);
        return ResponseEntity.status(HttpStatus.OK).body(getServerResponse(ServerResponseCode.SUCCESS, jobState));
    }

    @GetMapping("getAllJobs")
    public ResponseEntity<List<CrawlSchedulerJobInfo>> getAllJobs() throws SchedulerException {
        List<CrawlSchedulerJobInfo> jobList = jobService.getAllJobList();
        return ResponseEntity.status(HttpStatus.OK).body(jobList);
    }

    @GetMapping("getAllJobs2")
    public ResponseEntity<List<Map<String, Object>>> getAllJobs2() throws SchedulerException {
        List<Map<String, Object>> jobList = jobService.getAllJobs();
        return ResponseEntity.status(HttpStatus.OK).body(jobList);
    }

    @GetMapping("metaData")
    public ResponseEntity<SchedulerMetaData> metaData() throws SchedulerException {
        SchedulerMetaData metaData = jobService.getMetaData();
        return ResponseEntity.status(HttpStatus.OK).body(metaData);
    }

    private ServerResponse getServerResponse(int responseCode, Object data) {
        return getServerResponse(responseCode, null, data);
    }

    private ServerResponse getServerResponse(int responseCode, String message, Object data) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setStatusCode(responseCode);
        serverResponse.setMessage(message);
        serverResponse.setData(data);
        return serverResponse;
    }
}
