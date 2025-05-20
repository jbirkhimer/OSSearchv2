package edu.si.ossearch.reports.repository;

import edu.si.ossearch.reports.entity.SearchLogSummary;
import edu.si.ossearch.reports.entity.SearchLogSummaryId;
import edu.si.ossearch.reports.entity.projections.SearchLogSummaryFullProjection;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for accessing the search_log_summary table which provides
 * collection search counts for the past 30 days.
 */
@Tag(name = "SearchLogSummary", description = "SearchLogSummaryRepository")
@RepositoryRestResource(
    collectionResourceRel = "searchlogsummary",
    path = "searchlogsummary",
    excerptProjection = SearchLogSummaryFullProjection.class
)
@SecurityRequirement(name = "bearerAuth")
public interface SearchLogSummaryRepository extends JpaRepository<SearchLogSummary, SearchLogSummaryId> {

    @RestResource(path = "byCollectionId", rel = "customFindMethod")
    @Query("SELECT SUM(s.searchCount) FROM SearchLogSummary s WHERE s.collectionId = :collectionId " +
           "AND s.logDate >= :startDate")
    Long getSearchCountByCollectionId(@Param("collectionId") Integer collectionId,
                                     @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate);

    @RestResource(path = "totalLast30Days", rel = "customFindMethod")
    @Query("SELECT SUM(s.searchCount) FROM SearchLogSummary s WHERE s.logDate >= :startDate")
    Long getTotalSearchCountLast30Days(@Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate);

    @RestResource(exported = false)
    @Query("SELECT s.collectionId as collectionId, SUM(s.searchCount) as searchCount FROM SearchLogSummary s " +
           "WHERE s.logDate >= :startDate GROUP BY s.collectionId ORDER BY SUM(s.searchCount) DESC")
    List<Object[]> getAllCollectionSearchCountsLast30Days(@Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate);

    @RestResource(path = "findByDate", rel = "customFindMethod")
    List<SearchLogSummary> findByLogDate(@Param("logDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate);

    @RestResource(path = "findByCollectionIdAndDateRange", rel = "customFindMethod")
    List<SearchLogSummary> findByCollectionIdAndLogDateBetween(
        @Param("collectionId") Integer collectionId,
        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @RestResource(path = "findByDateRange", rel = "customFindMethod")
    List<SearchLogSummary> findByLogDateBetween(@Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @RestResource(path = "deleteOlderThan", rel = "customFindMethod")
    @Query("DELETE FROM SearchLogSummary s WHERE s.logDate < :cutoffDate")
    void deleteByLogDateBefore(@Param("cutoffDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate);

    // Add method to find by both ID components
    @RestResource(path = "byCollectionIdAndLogDate", rel = "customFindMethod")
    SearchLogSummary findByCollectionIdAndLogDate(
        @Param("collectionId") Integer collectionId,
        @Param("logDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate);

    @RestResource(path = "bySite", rel = "customFindMethod")
    List<SearchLogSummary> findBySite(@Param("site") String site);

    @RestResource(path = "bySiteAndDateRange", rel = "customFindMethod")
    List<SearchLogSummary> findBySiteAndLogDateBetween(
        @Param("site") String site,
        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @RestResource(path = "countBySite", rel = "customFindMethod")
    @Query("SELECT SUM(s.searchCount) FROM SearchLogSummary s WHERE s.site = :site AND s.logDate >= :startDate")
    Long getSearchCountBySite(
        @Param("site") String site,
        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate);

    @RestResource(path = "siteStats", rel = "customFindMethod")
    @Query("SELECT s.site, SUM(s.searchCount) as total FROM SearchLogSummary s " +
           "WHERE s.logDate >= :startDate GROUP BY s.site ORDER BY total DESC")
    List<Object[]> getSiteStats(@Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate);
}