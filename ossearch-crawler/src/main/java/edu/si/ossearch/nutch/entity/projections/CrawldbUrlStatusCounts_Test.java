package edu.si.ossearch.nutch.entity.projections;

/**
 * @author jbirkhimer
 */

//import edu.si.ossearch.nutch.entity.Webpage;
//import org.springframework.data.rest.core.config.Projection;

//@Projection(name = "crawldbUrlStatusCounts", types = {Webpage.class})
public interface CrawldbUrlStatusCounts_Test {
    String getStatusName();
    Long getStatusNameCount();
}
