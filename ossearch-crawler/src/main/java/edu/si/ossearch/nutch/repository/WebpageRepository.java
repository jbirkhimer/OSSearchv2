package edu.si.ossearch.nutch.repository;

import edu.si.ossearch.nutch.entity.Webpage;
import edu.si.ossearch.nutch.entity.WebpagePK;
import edu.si.ossearch.nutch.entity.projections.WebpageUrlStatusCounts;
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

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Tag(name = "Webpage", description = "WebpageRepository")
@RepositoryRestResource(collectionResourceRel = "webpage", path = "webpage")
@SecurityRequirement(name = "bearerAuth")
public interface WebpageRepository extends JpaRepository<Webpage, WebpagePK> {


    @Query("SELECT new edu.si.ossearch.nutch.entity.projections.WebpageUrlStatusCounts(w.statusName, COUNT(w.statusName)) FROM Webpage w WHERE w.collectionId = :collectionId AND ((:search is null or w.url like %:search%) AND (:statusName is null or w.statusName like %:statusName%)) GROUP BY w.statusName")
    Optional<List<WebpageUrlStatusCounts>> getCrawldbStats(@Param("collectionId") Integer collectionId, @Param("search") String search, @Param("statusName") String statusName);

    Optional<List<Webpage>> findAllByCollectionId(@Param("collectionId") Integer collectionId);

    @Query("SELECT w.id FROM Webpage w WHERE w.collectionId = :collectionId")
    Optional<List<String>> findAllUrlUuidsByCollectionId(@Param("collectionId") Integer collectionId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Webpage w WHERE w.collectionId = :collectionId")
    int deleteAllByCollectionId(@Param("collectionId") Integer collectionId);

    @Transactional
    @Modifying
    int deleteWebpagesByCollectionIdAndUrlIn(@Param("collectionId") Integer collectionId, @Param("urls") List<String> urls);

    @RestResource(path = "findByWebPagesByCollectionId", rel = "customFindMethod")
    //@Query("SELECT w FROM Webpage w WHERE w.collectionId = :collectionId AND (:search is null or (w.url like %:search% or w.statusName like %:search%))")
    @Query("SELECT w FROM Webpage w WHERE w.collectionId = :collectionId AND ((:search is null or w.url like %:search%) AND (:statusName is null or w.statusName like %:statusName%))")
    Optional<Page<Webpage>> findByWebPagesByCollectionId(@Param("collectionId") Integer collectionId, @Param("search") String search, @Param("statusName") String statusName, Pageable pageable);

    @RestResource(path = "findByWebPagesByCollectionIdReport", rel = "customFindMethod")
//    @Query("SELECT w FROM Webpage w WHERE w.collectionId = :collectionId AND (:search is null or (w.url like %:search% or w.statusName like %:search%))")
    @Query("SELECT w FROM Webpage w WHERE w.collectionId = :collectionId AND ((:search is null or w.url like %:search%) AND (:statusName is null or w.statusName like %:statusName%))")
    Optional<List<Webpage>> findByWebPagesByCollectionId(@Param("collectionId") Integer collectionId, @Param("search") String search, @Param("statusName") String statusName, Sort sort);


    Optional<Webpage> findWebpageByCollectionIdAndAndUrl(@Param("collectionId") Integer collectionId, @Param("url") String url);
}
