package edu.si.ossearch.nutch.entity.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jbirkhimer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrawldbUrlStatusCounts {
    private String statusName;
    private Long statusCount;
}

//@Projection(name = "crawldbUrlStatusCounts", types = {CrawldbUrlStatusCounts.class})
//public interface CrawldbUrlStatusCounts {
//    String getStatusName();
//    Long getStatusCount();
//}
