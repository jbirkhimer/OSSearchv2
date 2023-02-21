package edu.si.ossearch.reports.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.si.ossearch.collection.repository.CollectionRepository;
import edu.si.ossearch.reports.entity.projections.SearchLogChart;
import edu.si.ossearch.reports.repository.SearchLogRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

import javax.persistence.Tuple;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jbirkhimer
 */
@Slf4j
@RepositoryRestController
@SecurityRequirement(name = "bearerAuth")
public class SearchLogsController {

    @Autowired
    SearchLogRepository searchLogRepository;

    @Autowired
    CollectionRepository collectionRepository;

    private final ObjectMapper mapObject = new ObjectMapper();

    @GetMapping("/searchlog/search/countsByCollection")
    public @ResponseBody ResponseEntity<?> countsByCollection() {
        return ResponseEntity.ok(searchLogRepository.countsByCollection());
    }

    @GetMapping("/searchlog/search/keywordCountsBetweenDatesByCollectionId")
    public @ResponseBody ResponseEntity<?> keywordCountsBetweenDatesByCollectionId(@RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate,
                                                                                   @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate,
                                                                                   @RequestParam(value = "collectionId") Long collectionId,
                                                                                   @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText,
                                                                                   Pageable pageable,
                                                                                   ServletWebRequest webRequest) {

        Method enclosingMethod = new Object() {}.getClass().getEnclosingMethod();
        Sort sort = new SortHandlerMethodArgumentResolver().resolveArgument(new MethodParameter(enclosingMethod, 4), null, webRequest, null);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "query"));
        }

        return ResponseEntity.ok(searchLogRepository.keywordCountsBetweenDatesByCollectionId(startDate, endDate, collectionId, searchText, pageable));
    }

    @GetMapping("/searchlog/search/searchLogChartData")
    public @ResponseBody ResponseEntity<?> searchLogChartData(@RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date startDate,
                                                                                   @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date endDate,
                                                                                   @RequestParam(value = "collectionId", required = false) Long collectionId,
                                                                                   @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText) {

        List<CollectionRepository.CollectionIdNameInfoTest> userCollections = collectionRepository.findAllCollectionsByOwnerAndUsers();
        List<Long> collectionIds = userCollections.stream()
                .filter(collection -> collectionId != null ? collection.getId() == collectionId : true)
                .map(collection -> collection.getId())
                .collect(Collectors.toList());

        List<SearchLogChart> chartData = searchLogRepository.searchLogChartData(collectionIds, startDate, endDate, searchText);

        Map<String, List<SearchLogChart>> siteGrouped = chartData.stream()
                .filter(row -> row.getSite() != null && !row.getSite().isEmpty())
                .collect(Collectors.groupingBy(SearchLogChart::getSite));

        return ResponseEntity.ok(siteGrouped);
    }

    public static List<Map<String, Object>> jpaTuplesToMaps(List<Tuple> data) {
        return data.stream() // List<Tuple> -> Tuple1,..TupleN
                .map(tuple -> jpaTupleToMap((Tuple) tuple)) // Tuple1 -> HashMap1,..TupleN -> HashMapN
                .collect(Collectors.toList()); // HashMap1,..HashMapN -> List
    }

    public static Map<String, Object> jpaTupleToMap(Tuple data){
        Map<String, Object> result = new HashMap<>(); // exactly HashMap since it can handle NULL keys & values
        data.getElements().forEach(col -> result.put(col.getAlias(), data.get(col)));
        return result;
    }
}
