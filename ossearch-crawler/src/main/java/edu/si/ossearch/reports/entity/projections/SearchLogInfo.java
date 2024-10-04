package edu.si.ossearch.reports.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.reports.entity.SearchLog;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

/**
 * A Projection for the {@link SearchLog} entity
 */
@Projection(name = "searchLogInfo", types = { SearchLog.class })
public interface SearchLogInfo {
    String getQuery();

//    String getFields();

//    Integer getNumRows();

//    Long getStart();

//    String getResponseType();

    Long getDocsFound();

    Long getElapsedTime();

//    String getRequestIp();

    String getErrors();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedDate();
}
