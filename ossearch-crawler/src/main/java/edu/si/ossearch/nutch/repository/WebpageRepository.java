package edu.si.ossearch.nutch.repository;

import edu.si.ossearch.nutch.entity.CrawlDb;
import edu.si.ossearch.nutch.entity.Webpage;
import edu.si.ossearch.nutch.entity.projections.CrawldbUrlStatusCounts;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Tag(name = "Webpage", description = "WebpageRepository")
@RepositoryRestResource(collectionResourceRel = "webpage", path = "webpage")
@SecurityRequirement(name = "bearerAuth")
public interface WebpageRepository extends JpaRepository<Webpage, String> {


    @Query("SELECT new edu.si.ossearch.nutch.entity.projections.CrawldbUrlStatusCounts(w.statusName, COUNT(w.statusName)) FROM Webpage w WHERE w.crawlDb.collectionId = :collectionId GROUP BY w.statusName")
    Optional<List<CrawldbUrlStatusCounts>> getCrawldbStats(@Param("collectionId") Integer collectionId);

    Optional<List<Webpage>> findAllByCrawlDb_CollectionId(@Param("collectionId") Integer collectionId);

    @Query("SELECT w.id FROM Webpage w WHERE w.crawlDb.collectionId = :collectionId")
    Optional<List<String>> findAllUrlUuidsByCrawlDb_CollectionId(@Param("collectionId") Integer collectionId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Webpage w WHERE w.crawlDb = :crawlDb")
    int deleteAllByCrawlDb(@Param("crawlDb") CrawlDb crawlDb);

    @RestResource(path = "findByWebPagesByCrawlDb_CollectionId", rel = "customFindMethod")
    @Query("SELECT w FROM Webpage w WHERE w.crawlDb.collectionId = :collectionId AND (:search is null or (w.url like %:search% or w.statusName like %:search%))")
    Optional<Page<Webpage>> findByWebPagesByCrawlDb_CollectionId(@Param("collectionId") Integer collectionId, @Param("search") String search, Pageable pageable);

    @RestResource(path = "findByWebPagesByCrawlDb_CollectionIdReport", rel = "customFindMethod")
    @Query("SELECT w FROM Webpage w WHERE w.crawlDb.collectionId = :collectionId AND (:search is null or (w.url like %:search% or w.statusName like %:search%))")
    Optional<List<Webpage>> findByWebPagesByCrawlDb_CollectionId(@Param("collectionId") Integer collectionId, @Param("search") String search, Sort sort);


    Optional<Webpage> findWebpageByCrawlDb_CollectionIdAndAndUrl(@Param("collectionId") Integer collectionId, @Param("url") String url);
}