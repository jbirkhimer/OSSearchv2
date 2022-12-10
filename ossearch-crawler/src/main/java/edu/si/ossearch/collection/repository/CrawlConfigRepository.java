package edu.si.ossearch.collection.repository;

import edu.si.ossearch.collection.entity.CrawlConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "CrawlConfig", description = "CrawlConfigRepository")
@RepositoryRestResource(collectionResourceRel = "crawlConfig", path = "crawl-config")
@SecurityRequirement(name = "bearerAuth")
public interface CrawlConfigRepository extends JpaRepository<CrawlConfig, Long> {

    @Operation(summary = "Find crawl config by collection name")
    CrawlConfig getCrawlConfigByCollectionName(@Param("name") String name);

}