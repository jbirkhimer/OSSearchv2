package edu.si.ossearch.utils.reindex.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.si.ossearch.collection.repository.CollectionRepository;
import edu.si.ossearch.scheduler.controller.CrawlSchedulerController;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import edu.si.ossearch.scheduler.entity.ServerResponse;
import edu.si.ossearch.scheduler.repository.CrawlSchedulerJobInfoRepository;
import edu.si.ossearch.scheduler.service.JobService;
import edu.si.ossearch.utils.reindex.request.ReindexRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.io.File;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class ReindexServiceImpl implements ReindexService {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    CrawlSchedulerJobInfoRepository crawlSchedulerJobInfoRepository;

    @Value(value = "${ossearch.nutch.crawlDir}")
    File crawlDir;

    @Autowired
    EntityLinks entityLinks;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    CrawlSchedulerController crawlSchedulerController;

    @Autowired
    private JobService jobService;

    public static final String DEFAULT_JOB_GROUP_NAME = "scheduled_crawl";

    @Override
    @Transactional
    public ResponseEntity<?> batchReindex(List<ReindexRequest> reindexRequestList) {
        try {

            JSONArray response = new JSONArray();

            reindexRequestList.forEach(reindexRequest -> {

                String jobName = reindexRequest.getJobName();
                String jobGroup = reindexRequest.getGroupName();

                if (jobGroup == null || jobGroup.isEmpty()) {
                    reindexRequest.setGroupName(DEFAULT_JOB_GROUP_NAME);
                }

                JSONObject status = new JSONObject();
                response.put(status);
                status.put("jobName", jobName);
                status.put("jobGroup", jobGroup);

                if (!jobService.isJobRunning(jobName, jobGroup)) {

                    CrawlSchedulerJobInfo jobInfo = crawlSchedulerJobInfoRepository.findByJobNameAndJobGroup(jobName, jobGroup);

                    jobInfo.setReindex(true);
                    jobInfo.setJobType(CrawlSchedulerJobInfo.JobType.REINDEX);
                    jobInfo.setRecrawl(false);

                    crawlSchedulerJobInfoRepository.save(jobInfo);

                    boolean updateStatus = jobService.updateScheduleJob(jobInfo);

                    if (updateStatus) {
                        if (!jobService.isJobRunning(jobName, jobGroup)) {
                            boolean startStatus = jobService.startJobNow(jobName, jobGroup);

                            if (startStatus) {
                                status.put("status", "started");
                            } else {
                                status.put("status", "failed to start");
                            }

                        } else {
                            //Job already running
                            status.put("status", "Crawl Already Running!");
                        }
                    } else {
                        status.put("status", "failed to update");
                    }
                } else {
                    status.put("status", "Crawl Already Running!");
                }
            });
            return ResponseEntity.status(HttpStatus.CREATED).body(response.toString());
        } catch (Exception e) {
            log.error("Fail to upload files!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
