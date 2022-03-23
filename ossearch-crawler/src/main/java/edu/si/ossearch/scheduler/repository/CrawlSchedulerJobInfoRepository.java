package edu.si.ossearch.scheduler.repository;

import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author jbirkhimer
 */

@Tag(name = "CrawlSchedulerJobInfo", description = "CrawlSchedulerJobInfoRepository")
//@Repository
@RepositoryRestResource(collectionResourceRel = "crawlschedulerjobinfo", path = "crawlschedulerjobinfo")
@SecurityRequirement(name = "bearerAuth")
public interface CrawlSchedulerJobInfoRepository extends JpaRepository<CrawlSchedulerJobInfo, Long> {

    CrawlSchedulerJobInfo findByJobName(String jobName);

    CrawlSchedulerJobInfo findByJobNameAndJobGroup(String jobName, String jobGroup);

}
