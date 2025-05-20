package edu.si.ossearch.reports.entity.projections;

import edu.si.ossearch.reports.entity.SearchLogSummary;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Projection to expose all fields including composite key fields for SearchLogSummary
 */
@Projection(name = "fullSummary", types = { SearchLogSummary.class })
public interface SearchLogSummaryFullProjection {
    Integer getCollectionId();
    LocalDate getLogDate();
    String getSite();
    Long getSearchCount();
    LocalDateTime getLastUpdated();
}
