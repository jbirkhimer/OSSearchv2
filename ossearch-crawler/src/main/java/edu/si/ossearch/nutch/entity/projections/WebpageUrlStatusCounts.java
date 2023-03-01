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
public class WebpageUrlStatusCounts {
    private String statusName;
    private Long statusCount;
}

//@Projection(name = "webpageUrlStatusCounts", types = {WebpageUrlStatusCounts.class})
//public interface WebpageUrlStatusCounts {
//    String getStatusName();
//    Long getStatusCount();
//}
