package edu.si.ossearch.scheduler.controller;

import edu.si.ossearch.scheduler.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author jbirkhimer
 */
@Slf4j
@RestController
@RequestMapping("/api/scheduler")
@Tag(description = "Crawl Scheduler Utils", name = "Crawl Scheduler Utils")
@SecurityRequirement(name = "none")
public class CrawlSchedulerStatus {

    @Autowired
    private JobService jobService;

    @Operation(summary = "shutdown check", responses = {@ApiResponse(content = @Content(mediaType = "text/plain"))})
    @GetMapping(value = "shutdownCheck", produces = "text/plain")
    public String shutdownCheck() {
        return jobService.shutdownCheck().getBody().toString();
    }

}
