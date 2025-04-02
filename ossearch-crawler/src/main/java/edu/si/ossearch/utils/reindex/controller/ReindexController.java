package edu.si.ossearch.utils.reindex.controller;

import edu.si.ossearch.utils.reindex.request.ReindexRequest;
import edu.si.ossearch.utils.reindex.service.ReindexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Slf4j
@RestController
@RequestMapping("/api/utils")
@Tag(description = "Utils | Reindex", name = "Utils | Reindex")
@SecurityRequirement(name = "bearerAuth")
public class ReindexController {

    @Autowired
    private ReindexService reindexService;

    @PostMapping(value = "/reindex")
    @Operation(summary = "reindex collections by list of jobNames and jobGroup", responses = {@ApiResponse(content = {@Content(mediaType = "application/json")})})
    public ResponseEntity<?> bulkReindexCollectionByIds(@Valid @RequestBody List<ReindexRequest> reIndexRequest) {
        try {
            return reindexService.batchReindex(reIndexRequest);
        } catch (Exception e) {
            log.error("Fail to reindex!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
