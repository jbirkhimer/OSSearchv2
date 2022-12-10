package edu.si.ossearch.reports.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.reports.entity.SearchLog;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

/**
 * A Projection for the {@link SearchLog} entity
 */
@Projection(name = "searchLogKeywordsView", types = { SearchLog.class })
public interface SearchLogKeywordsView {
    String getQuery();

    Long getDocsFound();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedDate();
}
