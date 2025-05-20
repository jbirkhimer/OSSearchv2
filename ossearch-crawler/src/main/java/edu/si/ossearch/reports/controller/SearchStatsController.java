package edu.si.ossearch.reports.controller;

import edu.si.ossearch.reports.service.SearchLogSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for search log summary operations
 */
@RestController
@RequestMapping("/api/search-stats")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Search Statistics", description = "APIs for search statistics")
@SecurityRequirement(name = "bearerAuth")
public class SearchStatsController {

    private final SearchLogSummaryService searchLogSummaryService;

    @GetMapping("/last-30-days")
    @Operation(summary = "Get search counts for the last 30 days")
    public ResponseEntity<Map<String, Object>> getLast30DaysStats(
            @RequestParam(required = false) Integer collectionId) {

        Map<String, Object> response = new HashMap<>();

        if (collectionId != null) {
            response.put("collectionId", collectionId);
            response.put("searchCount", searchLogSummaryService.getSearchCountByCollectionId(collectionId));
        } else {
            response.put("totalSearchCount", searchLogSummaryService.getTotalSearchCountLast30Days());

            // Get top 10 collections
            List<Map<String, Object>> topCollections = searchLogSummaryService.getTopCollectionsLast30Days(10)
                .stream()
                .map(result -> {
                    Map<String, Object> collection = new HashMap<>();
                    collection.put("collectionId", result[0]);
                    collection.put("searchCount", result[1]);
                    return collection;
                })
                .collect(Collectors.toList());

            response.put("topCollections", topCollections);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-site")
    @Operation(summary = "Get search stats by site for the last 30 days",
              description = "Returns search counts grouped by site")
    public ResponseEntity<List<Map<String, Object>>> getStatsBySite(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchLogSummaryService.getStatsBySite(limit));
    }

    @GetMapping("/by-site/all-time")
    @Operation(summary = "Get all-time search stats by site",
              description = "Returns all-time search counts grouped by site")
    public ResponseEntity<List<Map<String, Object>>> getAllTimeStatsBySite(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchLogSummaryService.getAllTimeStatsBySite(limit));
    }

    @GetMapping("/all-time")
    @Operation(summary = "Get all-time search counts")
    public ResponseEntity<Map<String, Object>> getAllTimeStats(
            @RequestParam(required = false) Integer collectionId) {

        Map<String, Object> response = new HashMap<>();

        if (collectionId != null) {
            response.put("collectionId", collectionId);
            response.put("searchCount", searchLogSummaryService.getAllTimeSearchCountByCollectionId(collectionId));
        } else {
            response.put("totalSearchCount", searchLogSummaryService.getTotalAllTimeSearchCount());

            // Get top 10 collections
            List<Map<String, Object>> topCollections = searchLogSummaryService.getTopAllTimeCollections(10)
                .stream()
                .map(summary -> {
                    Map<String, Object> collection = new HashMap<>();
                    collection.put("collectionId", summary.getCollectionId());
                    collection.put("site", summary.getSite());
                    collection.put("searchCount", summary.getSearchCount());
                    return collection;
                })
                .collect(Collectors.toList());

            response.put("topCollections", topCollections);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/populate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Populate summary tables from existing search logs",
              description = "Admin only - populates both 30-day and all-time summary tables")
    @ApiResponse(responseCode = "200", description = "Summary tables populated successfully")
    public ResponseEntity<Map<String, Object>> populateSummaries(
            @RequestParam(required = false, defaultValue = "false") boolean rollingOnly,
            @RequestParam(required = false, defaultValue = "false") boolean allTimeOnly) {

        Map<String, Object> result;

        if (rollingOnly) {
            Map<String, Object> stats = searchLogSummaryService.populateRollingSummary();
            result = Map.of(
                "message", "30-day rolling summary populated successfully",
                "stats", stats
            );
        } else if (allTimeOnly) {
            Map<String, Object> stats = searchLogSummaryService.populateAllTimeSummary();
            result = Map.of(
                "message", "All-time summary populated successfully",
                "stats", stats
            );
        } else {
            Map<String, Object> stats = searchLogSummaryService.populateAllSummaries();
            result = Map.of(
                "message", "All summary tables populated successfully",
                "stats", stats
            );
        }

        return ResponseEntity.ok(result);
    }
}
