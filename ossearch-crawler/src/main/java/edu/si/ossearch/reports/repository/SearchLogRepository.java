package edu.si.ossearch.reports.repository;

import edu.si.ossearch.reports.entity.SearchLog;
import edu.si.ossearch.reports.entity.projections.SearchLogChart;
import edu.si.ossearch.reports.entity.projections.SearchLogKeywordCountsView;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Tag(name = "SearchLog", description = "SearchLogRepository")
@RepositoryRestResource(collectionResourceRel = "searchlog", path = "searchlog")
@SecurityRequirement(name = "bearerAuth")
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    String collectionsByOwnerUserAdmin = "select distinct c.id from collection c left outer join collections_user cu on c.id = cu.collection_id left outer join users u2 on cu.user_id = u2.id cross join users u where c.owner_id = u.id and (u2.username = ?#{authentication.name} or u.username = ?#{authentication.name} or 1 = ?#{hasRole('ROLE_ADMIN') ? 1 : 0})";

    Page<SearchLog> findAllByCollectionId(@Param("collectionId") Long collectionId, Pageable pageable);

    @Override
    long count();

    long countByCollectionId(Long collectionId);

    Long countByCollectionIdIn(List<Long> collectionIds);

    @Query(value = "select count(s.id) from search_log s" +
            " where s.collection_id in (" + collectionsByOwnerUserAdmin + ")",
            nativeQuery = true)
    long totalCountForAllCollections();

    @Query(value = "select count(s.id) from search_log s" +
            " where s.collection_id in (" + collectionsByOwnerUserAdmin + ")" +
            " and s.created_date between DATE_ADD(CURDATE(), INTERVAL -?1 DAY)" +
            " and addtime(CURDATE(), '23:59:59') and collection_id = ?2",
            nativeQuery = true)
    long totalCountForAllCollectionsLastNumDaysByCollectionId(@Param("days") Integer days, @Param("collectionId") Long collectionId);

    @Query(value = "select count(s.id) from search_log s" +
            " where s.collection_id in (" + collectionsByOwnerUserAdmin + ")" +
            " and s.created_date between DATE_ADD(CURDATE(), INTERVAL -?1 DAY) and addtime(CURDATE(), '23:59:59')",
            nativeQuery = true)
    long totalCountForAllCollectionsLastNumDays(@Param("days") Integer days);

    @Query(value = "select count(s.id) from search_log s" +
            " where s.collection_id in (" + collectionsByOwnerUserAdmin + ")" +
            " and s.created_date between ?1 and ?2",
            nativeQuery = true)
    long totalCountForAllCollectionsBetweenDates(@Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate, @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate);

    @Query("select s from SearchLog s" +
            " where s.collectionId = :collectionId" +
            " and (s.query like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()}" +
            " or s.responseType like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()}" +
            " or s.requestIp like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()}" +
            " or s.docsFound like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()}" +
            " or s.elapsedTime like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()})" +
            " and s.createdDate between :startDate and :endDate")
    Optional<Page<SearchLog>> totalCountForAllCollectionsBetweenDatesByCollectionId(@Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate,
                                                                                    @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate,
                                                                                    @Param("collectionId") Long collectionId,
                                                                                    @Param("searchText") @Nullable String searchText,
                                                                                    Pageable pageable);

    @Query(value = "select count(s.id) as count, s.site as site, s.collection_id as collectionId from search_log s inner join (" + collectionsByOwnerUserAdmin + ") cj on cj.id = s.collection_id group by s.site, s.collection_id order by count desc", nativeQuery = true)
    List<CountsByCollectionView> countsByCollection();

    interface CountsByCollectionView {
        Integer getCount();

        String getSite();

        Integer getCollectionId();
    }


    @Query(value = "select s from SearchLog s" +
            " where s.collectionId = :collectionId" +
            " and (:searchText is null or s.query like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()})" +
            " and s.createdDate between :startDate and :endDate")
    Optional<Page<SearchLog>> keywordsBetweenDatesByCollectionId(@Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate,
                                                                 @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate,
                                                                 @Param("collectionId") Long collectionId,
                                                                 @Param("searchText") @Nullable String searchText,
                                                                 Pageable pageable);

    @Query(value = "select query, count(query) as wordCount, concat(date_format(min(created_date), '%Y-%m-%d'), ' - ', date_format(max(created_date), '%Y-%m-%d')) as dateRange from search_log" +
            " where collection_id = :collectionId" +
            " and (:searchText is null or query like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()})" +
            " and created_date between :startDate and :endDate" +
            " group by query",
            countQuery = "select count(sc.query) from (" +
                    " select query, count(query)" +
                    " from search_log" +
                    " where collection_id = :collectionId" +
                    " and (:searchText is null or query like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()})" +
                    " and created_date between :startDate and :endDate" +
                    " group by query) sc",
            nativeQuery = true)
    Optional<Page<SearchLogKeywordCountsView>> keywordCountsBetweenDatesByCollectionId(@Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate,
                                                                                       @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate,
                                                                                       @Param("collectionId") Long collectionId,
                                                                                       @Param("searchText") @Nullable String searchText,
                                                                                       Pageable pageable);



    /*@Query("SELECT YEAR(createdDate) as year, MONTHNAME(createdDate) as monthName, DATE(createdDate) as date, count(query) as count, site as site" +
            " FROM SearchLog" +
            " WHERE createdDate between :startDate and :endDate" +
            " GROUP BY year, monthName, date, site" +
            " ORDER BY site, date")
    List<SearchLogChart> searchLogChartData(@Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate,
                                                      @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate);*/

    @Query("SELECT new edu.si.ossearch.reports.entity.projections.SearchLogChart(DATE(createdDate), count(query), site)" +
            " FROM SearchLog" +
            " WHERE collectionId in :collectionIds" +
            " and (:searchText is null or query like CONCAT('%', :#{escape(#searchText)} , '%') escape :#{escapeCharacter()})" +
            " and createdDate between :startDate and :endDate" +
            " GROUP BY YEAR(createdDate), MONTHNAME(createdDate), DATE(createdDate), site" +
            " ORDER BY site, DATE(createdDate)")
    List<SearchLogChart> searchLogChartData(@Param("collectionIds") List<Long> collectionIds,
                                            @Param("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate,
                                            @Param("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate,
                                            @Param("searchText") @Nullable String searchText);


}
