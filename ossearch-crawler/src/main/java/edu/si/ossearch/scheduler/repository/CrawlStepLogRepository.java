package edu.si.ossearch.scheduler.repository;

import edu.si.ossearch.scheduler.entity.CrawlStepLog;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@Tag(name = "CrawlStepLog", description = "CrawlStepLogRepository")
//@Repository
@RepositoryRestResource(collectionResourceRel = "crawlsteplog", path = "crawlsteplog")
@SecurityRequirement(name = "bearerAuth")
public interface CrawlStepLogRepository extends JpaRepository<CrawlStepLog, Long> {

    List<CrawlStepLog> getCrawlStepLogsByJobId(@Param("jobId") String jobId, @Param("sort") Sort sort);
}