package edu.si.ossearch.reports.service;

import edu.si.ossearch.reports.entity.SearchLogAllTimeSummary;
import edu.si.ossearch.reports.entity.SearchLogSummary;
import edu.si.ossearch.reports.repository.SearchLogAllTimeSummaryRepository;
import edu.si.ossearch.reports.repository.SearchLogSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service to manage search log summary data and perform maintenance operations
 * with bulk processing for large datasets
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchLogSummaryService {

    private final SearchLogSummaryRepository searchLogSummaryRepository;
    private final SearchLogAllTimeSummaryRepository allTimeSummaryRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Get the total search count for all collections in the last 30 days
     */
    public Long getTotalSearchCountLast30Days() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return searchLogSummaryRepository.getTotalSearchCountLast30Days(thirtyDaysAgo);
    }

    /**
     * Get the search count for a specific collection in the last 30 days
     */
    public Long getSearchCountByCollectionId(Integer collectionId) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return searchLogSummaryRepository.getSearchCountByCollectionId(collectionId, thirtyDaysAgo);
    }

    /**
     * Get the total all-time search count
     */
    public Long getTotalAllTimeSearchCount() {
        return allTimeSummaryRepository.getTotalSearchCount();
    }

    /**
     * Get the all-time search count for a specific collection
     */
    public Long getAllTimeSearchCountByCollectionId(Integer collectionId) {
        SearchLogAllTimeSummary summary = allTimeSummaryRepository.findById(collectionId).orElse(null);
        return summary != null ? summary.getSearchCount() : 0L;
    }

    /**
     * Get collections with the highest search counts in the last 30 days
     * @param limit Maximum number of collections to return
     * @return List of collection IDs and their search counts
     */
    public List<Object[]> getTopCollectionsLast30Days(int limit) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return searchLogSummaryRepository.getAllCollectionSearchCountsLast30Days(thirtyDaysAgo).stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get collections with the highest all-time search counts
     * @param limit Maximum number of collections to return
     * @return List of SearchLogAllTimeSummary objects
     */
    public List<SearchLogAllTimeSummary> getTopAllTimeCollections(int limit) {
        return allTimeSummaryRepository.findAllOrderBySearchCountDesc().stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get statistics by site for the last 30 days
     */
    public List<Map<String, Object>> getStatsBySite(int limit) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        // Use the repository method instead of jdbcTemplate
        List<Object[]> siteStats = searchLogSummaryRepository.getSiteStats(thirtyDaysAgo);

        // Convert to Map<String, Object> for consistent response format
        return siteStats.stream()
            .limit(limit)
            .map(row -> {
                Map<String, Object> stat = new HashMap<>();
                stat.put("site", row[0]);
                stat.put("totalCount", row[1]);
                return stat;
            })
            .collect(Collectors.toList());
    }

    /**
     * Get all-time statistics by site
     */
    public List<Map<String, Object>> getAllTimeStatsBySite(int limit) {
        // Use the repository method
        List<Object[]> siteStats = allTimeSummaryRepository.getSiteStats();

        // Convert to Map<String, Object>
        return siteStats.stream()
            .limit(limit)
            .map(row -> {
                Map<String, Object> stat = new HashMap<>();
                stat.put("site", row[0]);
                stat.put("totalCount", row[1]);
                return stat;
            })
            .collect(Collectors.toList());
    }

    /**
     * Populate the 30-day rolling summary table from existing search_log data
     * using a single query followed by bulk save
     * @return Summary of the operation
     */
    @Transactional
    public Map<String, Object> populateRollingSummary() {
        log.info("Starting population of 30-day rolling summary table");

        // Clear existing data first if needed
        searchLogSummaryRepository.deleteAll();

        try {
            // Get all counts in a single query
            String sql =
                "SELECT collection_id, site, DATE(created_date) as log_date, COUNT(*) as search_count " +
                "FROM search_log " +
                "GROUP BY collection_id, site, DATE(created_date)";

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            log.info("Retrieved {} day-collection combinations", results.size());

            // Map to entities
            List<SearchLogSummary> summaries = results.stream()
                .map(row -> {
                    SearchLogSummary summary = new SearchLogSummary();
                    summary.setCollectionId(((Number) row.get("collection_id")).intValue());
                    summary.setSite((String) row.get("site"));
                    summary.setLogDate(((java.sql.Date) row.get("log_date")).toLocalDate());
                    summary.setSearchCount(((Number) row.get("search_count")).longValue());
                    return summary;
                })
                .collect(Collectors.toList());

            // Bulk save
            searchLogSummaryRepository.saveAll(summaries);
            log.info("Successfully saved {} 30-day rolling summary records", summaries.size());

            return Map.of(
                "status", "success",
                "recordCount", summaries.size()
            );

        } catch (Exception e) {
            log.error("Error populating 30-day rolling summary", e);
            return Map.of(
                "status", "error",
                "message", e.getMessage()
            );
        }
    }

    /**
     * Populate the all-time summary table from existing search_log data
     * using a single query followed by bulk save
     * @return Summary of the operation
     */
    @Transactional
    public Map<String, Object> populateAllTimeSummary() {
        log.info("Starting population of all-time summary table");

        // Clear existing data first
        allTimeSummaryRepository.deleteAll();

        try {
            // Get all counts in a single query
            String sql =
                "SELECT collection_id, site, COUNT(*) as search_count " +
                "FROM search_log " +
                "GROUP BY collection_id, site";

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            log.info("Retrieved {} collection total counts", results.size());

            // Map to entities
            List<SearchLogAllTimeSummary> summaries = results.stream()
                .map(row -> {
                    SearchLogAllTimeSummary summary = new SearchLogAllTimeSummary();
                    summary.setCollectionId(((Number) row.get("collection_id")).intValue());
                    summary.setSite((String) row.get("site"));
                    summary.setSearchCount(((Number) row.get("search_count")).longValue());
                    summary.setLastUpdated(LocalDateTime.now());
                    return summary;
                })
                .collect(Collectors.toList());

            // Bulk save
            allTimeSummaryRepository.saveAll(summaries);
            log.info("Successfully saved {} all-time summary records", summaries.size());

            return Map.of(
                "status", "success",
                "recordCount", summaries.size()
            );

        } catch (Exception e) {
            log.error("Error populating all-time summary", e);
            return Map.of(
                "status", "error",
                "message", e.getMessage()
            );
        }
    }

    /**
     * Populate both summary tables from existing search_log data
     * @return Summary information about the operation
     */
    public Map<String, Object> populateAllSummaries() {
        Map<String, Object> rollingResult = populateRollingSummary();
        Map<String, Object> allTimeResult = populateAllTimeSummary();

        Map<String, Object> combinedResult = new HashMap<>();
        combinedResult.put("rollingResult", rollingResult);
        combinedResult.put("allTimeResult", allTimeResult);

        return combinedResult;
    }
}