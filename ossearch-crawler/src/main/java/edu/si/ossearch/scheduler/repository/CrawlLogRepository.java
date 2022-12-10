package edu.si.ossearch.scheduler.repository;

import edu.si.ossearch.scheduler.entity.CrawlLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

@Tag(name = "CrawlLog", description = "CrawlLogRepository")
//@Repository
@RepositoryRestResource(collectionResourceRel = "crawllog", path = "crawllog")
@SecurityRequirement(name = "bearerAuth")
public interface CrawlLogRepository extends JpaRepository<CrawlLog, Long> {

    @Operation(summary = "Get CrawlConfig For CrawlLog")
    @Query("select c.crawlConfig from CrawlLog c where c.id = :id")
    String getCrawlConfigByCrawlLogId(@Param("id") Long id);

    @Operation(summary = "Get JobConfig For CrawlLog")
    @Query("select c.jobConfig from CrawlLog c where c.id = :id")
    String getJobConfigByCrawlLogId(@Param("id") Long id);

    List<CrawlLog> getCrawlLogsByJobKey(@Param("jobKey") String jobKey);

    List<CrawlLog> getCrawlLogsByJobKeyEndsWith(@Param("jobName") String jobName);

    @Operation(summary = "Get Latest CrawlLog stats")
    @Query(nativeQuery = true, value = "select * from crawl_log cl,(SELECT cl3.job_key, MAX(cl3.updated_date) as updated_date FROM crawl_log cl3 WHERE cl3.state = 'FINISHED' GROUP BY cl3.job_key) cl2 where cl.job_key = cl2.job_key and cl.updated_date = cl2.updated_date")
    List<CrawlLog> getLatestCrawlLogStats();
}
