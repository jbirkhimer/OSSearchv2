package edu.si.ossearch.nutch.controller;

import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.nutch.service.CrawlUtilsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/crawl/utils")
@Tag(description = "Crawl Utils", name = "Crawl Utils")
@SecurityRequirement(name = "bearerAuth")
public class CrawlUtilsController {

    @Autowired
    private CrawlUtilsService crawlUtilsService;

    public static final String DEFAULT_JOB_GROUP_NAME = "scheduled_crawl";

    @Operation(summary = "add urls to collection", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/urls/add")
    public ResponseEntity<Object> addUrls(@RequestParam(value = "jobName") String jobName,
                                          @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup,
                                          @RequestParam(value = "crawl") boolean crawl,
                                          @RequestBody List<String> urls) throws Exception {

        try {
            crawlUtilsService.addUrls(jobName, jobGroup, crawl, urls);
        } catch (Exception | OSSearchException e) {
            log.error("Problem adding urls to collection {} crawldb", jobName, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "add urls to collection", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/urls/add/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addUrlsCsv(@RequestParam(value = "jobName") String jobName,
                                             @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup,
                                             @RequestParam(value = "crawl") boolean crawl,
                                             @RequestParam(required = false) MultipartFile file) throws Exception {

        // su apache -s /bin/bash -c '/opt/nutch-1.18/bin/nutch inject /opt/ossearch-crawler/crawls/postal_88/db/crawldb /opt/ossearch-crawler/crawls/postal_88/add-seeds.txt -overwrite -noNormalize -noFilter'
        try {

            List<String> urls = new ArrayList<>();

            if (file != null || !file.isEmpty()) {
                urls.addAll(crawlUtilsService.readAll(file));
            }

            crawlUtilsService.addUrls(jobName, jobGroup, crawl, urls);

        } catch (Exception | OSSearchException e) {
            log.error("Problem adding urls to collection {} crawldb", jobName, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "remove urls from collection", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/urls/remove")
    public ResponseEntity<Object> removeUrls(@RequestParam(value = "jobName") String jobName,
                                             @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup,
                                             @RequestBody(required = true) List<String> urls) {

        try {
            crawlUtilsService.removeUrls(jobName, jobGroup, urls);
        } catch (Exception | OSSearchException e) {
            log.error("Problem removing urls from collection {}", jobName, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "remove urls from collection", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PostMapping(value = "/urls/remove/csv")
    public ResponseEntity<Object> removeUrlsCsv(@RequestParam(value = "jobName") String jobName,
                                                @RequestParam(value = "jobGroup", required = false, defaultValue = DEFAULT_JOB_GROUP_NAME) String jobGroup,
                                                @RequestParam(required = false) MultipartFile file) {
        try {

            List<String> urls = new ArrayList<>();

            if (file != null || !file.isEmpty()) {
                urls.addAll(crawlUtilsService.readAll(file));
            }

            crawlUtilsService.removeUrls(jobName, jobGroup, urls);

        } catch (Exception | OSSearchException e) {
            log.error("Problem removing urls from collection {}", jobName, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
