package edu.si.ossearch.reports.repository;

import edu.si.ossearch.reports.entity.SearchLogAllTimeSummary;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Repository for accessing the search_log_all_time_summary table.
 */
@Tag(name = "SearchLogAllTimeSummary", description = "SearchLogAllTimeSummaryRepository")
@RepositoryRestResource(collectionResourceRel = "searchlogalltimesummary", path = "searchlogalltimesummary")
@SecurityRequirement(name = "bearerAuth")
public interface SearchLogAllTimeSummaryRepository extends JpaRepository<SearchLogAllTimeSummary, Integer> {

    @RestResource(path = "total", rel = "customFindMethod")
    @Query("SELECT SUM(s.searchCount) FROM SearchLogAllTimeSummary s")
    Long getTotalSearchCount();

    @RestResource(path = "allCollections", rel = "customFindMethod")
    @Query("SELECT s FROM SearchLogAllTimeSummary s ORDER BY s.searchCount DESC")
    List<SearchLogAllTimeSummary> findAllOrderBySearchCountDesc();

    // New site-related methods
    @RestResource(path = "bySite", rel = "customFindMethod")
    List<SearchLogAllTimeSummary> findBySite(@Param("site") String site);

    @RestResource(path = "countBySite", rel = "customFindMethod")
    @Query("SELECT SUM(s.searchCount) FROM SearchLogAllTimeSummary s WHERE s.site = :site")
    Long getSearchCountBySite(@Param("site") String site);

    @RestResource(path = "siteStats", rel = "customFindMethod")
    @Query("SELECT s.site, SUM(s.searchCount) as total FROM SearchLogAllTimeSummary s " +
           "GROUP BY s.site ORDER BY total DESC")
    List<Object[]> getSiteStats();
}
