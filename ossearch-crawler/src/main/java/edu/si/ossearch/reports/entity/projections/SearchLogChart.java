package edu.si.ossearch.reports.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.util.Date;

/**
 * @author jbirkhimer
 */
@Value
public class SearchLogChart {
//    Integer year;
//    String monthName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date date;
    Long count;
    String site;
}
