package edu.si.ossearch.edan.controller;

import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.edan.service.EDANFieldMappingUtilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@EnableAsync
@RestController
@RequestMapping("/api/edan/utils")
@Tag(description = "EDAN Field Mapping Utils", name = "EDAN Field Mapping Utils")
@SecurityRequirement(name = "bearerAuth")
public class EDANFieldMappingUtilController {

    @Autowired
    private EDANFieldMappingUtilService edanFieldMappingUtilService;

    @Operation(
            summary = "EDAN Field Mapping Util",
            responses = {@ApiResponse(content = @Content(mediaType = "application/json"))},
            description = "EDAN Field Mapping Util for getting a collections fields available (indexed) to be mapped to EDAN"
    )
    @GetMapping(value = "/indexedFields/{collectionId}")
    public ResponseEntity<Object> getIndexedFields(@PathVariable(name = "collectionId", required = true) Long collectionID) throws OSSearchException {

        if (collectionID == null) {
            return ResponseEntity.badRequest().body("collectionID must not be null");
        }

        try {

            List<Map<String, Object>> output = edanFieldMappingUtilService.loadMetaTagsFromSolr(collectionID);

            return ResponseEntity.ok(output);
        } catch (Exception e) {
            log.error("indexedFields problem for collectionID: ", collectionID, e);
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
        }
    }
}
