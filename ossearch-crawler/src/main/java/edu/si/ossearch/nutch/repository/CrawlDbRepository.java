package edu.si.ossearch.nutch.repository;

import edu.si.ossearch.nutch.entity.CrawlDb;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@Tag(name = "Crawldb", description = "CrawldbRepository")
@RepositoryRestResource(collectionResourceRel = "crawldb", path = "crawldb")
@SecurityRequirement(name = "bearerAuth")
public interface CrawlDbRepository extends JpaRepository<CrawlDb, Long> {
    Optional<CrawlDb> findCrawlDbByCollectionId(Integer collectionId);
}