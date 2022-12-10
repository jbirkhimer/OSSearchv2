package edu.si.ossearch.reports.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.si.ossearch.collection.repository.CollectionRepository;
import edu.si.ossearch.nutch.entity.projections.CrawldbUrlStatusCounts;
import edu.si.ossearch.reports.service.ReportsService;
import edu.si.ossearch.reports.util.SortOrderParser;
import edu.si.ossearch.reports.entity.SearchLog;
import edu.si.ossearch.reports.entity.projections.SearchLogKeywordCountsView;
import edu.si.ossearch.reports.repository.SearchLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.*;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.persistence.Tuple;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jbirkhimer
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
@Tag(description = "Reports", name = "Reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportsController {

    @Autowired
    SearchLogRepository searchLogRepository;

    @Autowired
    private ReportsService reportsService;

    @Autowired
    CollectionRepository collectionRepository;

    private final ObjectMapper mapObject = new ObjectMapper();

    @GetMapping(value = "/keywords/{collectionId}")
    @Operation(summary = "get keywords", responses = {@ApiResponse( content = {@Content(mediaType = "application/json"), @Content(mediaType = "application/csv"), @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), @Content(mediaType = "application/pdf")})})
    public @ResponseBody ResponseEntity<?> keywordsBetweenDatesByCollectionId(@RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                                              @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
                                                                              @RequestParam(value = "collectionId") Long collectionId,
                                                                              @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText,
                                                                              @RequestParam(value = "exportType", required = false) String exportType,
                                                                              Pageable pageable,
                                                                              ServletWebRequest webRequest) {

        Method enclosingMethod = new Object() {}.getClass().getEnclosingMethod();
        Sort sort = new SortHandlerMethodArgumentResolver().resolveArgument(new MethodParameter(enclosingMethod, 4), null, webRequest, null);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "query"));
        }

        Page<SearchLog> searchLogs = searchLogRepository.keywordsBetweenDatesByCollectionId(startDate, endDate, collectionId, searchText, pageable).orElse(new PageImpl<>(new ArrayList<>()));

        List<Map<String, Object>> results = searchLogs.stream()
                .map((SearchLog row) -> (Map<String, Object>) mapObject.convertValue(row, Map.class))
                .collect(Collectors.toList());

        Optional<String> collectionName = collectionRepository.findCollectionById(collectionId);
        String filePrefix = collectionName.get() + "_" + collectionId + "_KeywordsReport";
        return getResponse(exportType, filePrefix, results);
    }

    @GetMapping("/keywordCounts/{collectionId}")
    @Operation(summary = "get keyword counts", responses = {@ApiResponse( content = {@Content(mediaType = "application/json"), @Content(mediaType = "application/csv"), @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), @Content(mediaType = "application/pdf")})})
    public @ResponseBody ResponseEntity<?> keywordCountsBetweenDatesByCollectionIdExport(@RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                                                         @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
                                                                                         @RequestParam(value = "collectionId") Long collectionId,
                                                                                         @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText,
                                                                                         @RequestParam(value = "exportType", required = false) String exportType,
                                                                                         Pageable pageable,
                                                                                         ServletWebRequest webRequest) {

        Method enclosingMethod = new Object() {}.getClass().getEnclosingMethod();
        Sort sort = new SortHandlerMethodArgumentResolver().resolveArgument(new MethodParameter(enclosingMethod, 4), null, webRequest, null);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "query"));
        }

        Page<SearchLogKeywordCountsView> searchLogs = searchLogRepository.keywordCountsBetweenDatesByCollectionId(startDate, endDate, collectionId, searchText, pageable).orElse(new PageImpl<>(new ArrayList<>()));
        List<Map<String, Object>> results = searchLogs.stream()
                .map((SearchLogKeywordCountsView row) -> (Map<String, Object>) mapObject.convertValue(row, Map.class))
                .collect(Collectors.toList());
        Optional<String> collectionName = collectionRepository.findCollectionById(collectionId);
        String filePrefix = collectionName.get() + "_" + collectionId + "_KeywordsCountsReport";
        return getResponse(exportType, filePrefix, results);
    }

    Sort parseParameterIntoSort(List<String> source, String delimiter) {

        List<Sort.Order> allOrders = new ArrayList<>();

        for (String part : source) {

            if (part == null) {
                continue;
            }

            SortOrderParser.parse(part, delimiter) //
                    .parseIgnoreCase() //
                    .parseDirection() //
                    .forEachOrder(allOrders::add);
        }

        return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
    }

    public static List<Map<String, Object>> jpaTuplesToMaps(List<Tuple> data) {
        return data.stream() // List<Tuple> -> Tuple1,..TupleN
                .map(ReportsController::jpaTupleToMap) // Tuple1 -> HashMap1,..TupleN -> HashMapN
                .collect(Collectors.toList()); // HashMap1,..HashMapN -> List
    }

    public static Map<String, Object> jpaTupleToMap(Tuple data){
        Map<String, Object> result = new HashMap<>(); // exactly HashMap since it can handle NULL keys & values
        data.getElements().forEach(col -> result.put(col.getAlias(), data.get(col)));
        return result;
    }

    private ResponseEntity<?> getResponse(String exportType, String filePrefix, List<Map<String, Object>> results) {

        try {
            ByteArrayInputStream byteArrayOutputStream = reportsService.export(results, exportType, filePrefix);

            InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

            MediaType contentType = null;
            if (exportType == null || exportType.equalsIgnoreCase("csv")) {
                exportType = "csv";
                contentType = MediaType.parseMediaType("application/csv");
            } else if (exportType.equalsIgnoreCase("xlsx") || exportType.equalsIgnoreCase("xls")) {
                contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            } else if (exportType.equalsIgnoreCase("pdf")) {
                contentType = MediaType.parseMediaType("application/pdf");
            }

            String filename = filePrefix + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "." + exportType;

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(contentType)
                    .body(fileInputStream);
        } catch (IOException e) {
            log.error("Problem creating {}!", filePrefix, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem creating " + filePrefix);
        }
    }

    @GetMapping(value = "/solr/{collectionId}")
    @Operation(summary = "Solr Collection Record URL's Report", responses = {@ApiResponse( content = {@Content(mediaType = "application/json"), @Content(mediaType = "application/csv"), @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), @Content(mediaType = "application/pdf")})})
    public ResponseEntity<Object> getSolrCollectionUrlsById(@PathVariable(name = "collectionId") Integer collectionId,
                                                            @RequestParam(value = "export", required = false, defaultValue = "false") Boolean export,
                                                            @RequestParam(value = "exportType", required = false) String exportType,
                                                            @RequestParam(value = "search", required = false, defaultValue = "*:*") String search,
                                                            @RequestParam(value = "sort", defaultValue = "id asc") String sort,
                                                            @RequestParam(value = "page") Integer page,
                                                            @RequestParam(value = "rows") Integer rows,
                                                            @RequestParam(value = "cursorMark", required = false) Integer cursorMark) {

        JSONObject solrReport = null;
        try {

            if (export) {

                solrReport = reportsService.solrReport(collectionId, search, sort, 0, -1);

                Optional<String> collectionName = collectionRepository.findCollectionById(collectionId);
                String filePrefix = collectionName.get() + "_" + collectionId+ "_IndexedUrls";

                try {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) solrReport.toMap().get("data");
                    ByteArrayInputStream byteArrayOutputStream = reportsService.export(data, exportType, "IndexedUrls");

                    InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

                    String filename = filePrefix+ "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "." + exportType;

                    MediaType contentType = null;
                    if (exportType.equalsIgnoreCase("csv")) {
                        contentType = MediaType.parseMediaType("application/csv");
                    } else if (exportType.equalsIgnoreCase("xlsx") || exportType.equalsIgnoreCase("xls")) {
                        contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    } else if (exportType.equalsIgnoreCase("pdf")) {
                        contentType = MediaType.parseMediaType("application/pdf");
                    }

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                            .contentType(contentType)
                            .body(fileInputStream);
                } catch (IOException e) {
                    log.error("Problem creating {}!", filePrefix, e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem creating "+ filePrefix);
                }
            } else {
                solrReport = reportsService.solrReport(collectionId, search, sort, page, rows);
                return ResponseEntity.status(HttpStatus.OK).body(solrReport.toString());
            }
        } catch (SolrServerException | IOException e) {
            log.error("Collection Solr URl's Report Problem!", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/solr/url/{collectionId}")
    @Operation(summary = "Solr Collection Record URL's Report", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    public ResponseEntity<String> getSolrCollectionUrlById(@PathVariable(name = "collectionId") Integer collectionId,
                                                            @RequestParam(value = "url", required = true) String url) {

        JSONObject solrReport = null;
        try {
            solrReport = reportsService.solrUrlReport(collectionId, url);
        } catch (SolrServerException | IOException e) {
            log.error("Collection Solr URl's Report Problem!", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(solrReport.toString());
    }

    @GetMapping(value = "/crawldb/stats/{collectionId}")
    @Operation(summary = "CrawlDb Stats Report", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    public ResponseEntity<String> getCrawlDbStatsByCollectionId(@PathVariable(name = "collectionId") Integer collectionId) {

        Optional<List<CrawldbUrlStatusCounts>> crawldbStats = reportsService.crawldbStats(collectionId);


        JSONArray answer = new JSONArray(crawldbStats.get());

        return ResponseEntity.status(HttpStatus.OK).body(answer.toString());
    }

    @GetMapping(value = "/crawldb/{collectionId}")
    @Operation(summary = "CrawlDb Webpages Report", responses = {@ApiResponse( content = {@Content(mediaType = "application/json"), @Content(mediaType = "application/csv"), @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), @Content(mediaType = "application/pdf")})})
    public ResponseEntity<Object> getCrawlDbByCollectionId(@PathVariable(name = "collectionId") Integer collectionId,
                                                           @RequestParam(value = "export", required = false, defaultValue = "false") Boolean export,
                                                           @RequestParam(value = "exportType", required = false) String exportType,
                                                           @RequestParam(value = "search", required = false) String search,
                                                           @RequestParam(value = "sort", defaultValue = "id asc") List<String> sort,
                                                           @RequestParam(value = "page") Integer page,
                                                           @RequestParam(value = "rows") Integer rows) {

        JSONObject crawldbWebpages;

        if (export) {

            crawldbWebpages = reportsService.crawldbWebpages(collectionId, search, sort, 0, -1, export);

            Optional<String> collectionName = collectionRepository.findCollectionById(collectionId);
            String filePrefix = collectionName.get() + "_" + collectionId+ "_crawldb";

            try {
                List<Map<String, Object>> data = (List<Map<String, Object>>) crawldbWebpages.toMap().get("data");
                ByteArrayInputStream byteArrayOutputStream = reportsService.export(data, exportType, "IndexedUrls");

                InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

                String filename = filePrefix+ "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "." + exportType;

                MediaType contentType = null;
                if (exportType.equalsIgnoreCase("csv")) {
                    contentType = MediaType.parseMediaType("application/csv");
                } else if (exportType.equalsIgnoreCase("xlsx") || exportType.equalsIgnoreCase("xls")) {
                    contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                } else if (exportType.equalsIgnoreCase("pdf")) {
                    contentType = MediaType.parseMediaType("application/pdf");
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                        .contentType(contentType)
                        .body(fileInputStream);
            } catch (IOException e) {
                log.error("Problem creating {}!", filePrefix, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem creating "+ filePrefix);
            }
        } else {

            crawldbWebpages = reportsService.crawldbWebpages(collectionId, search, sort, page, rows, export);
            return ResponseEntity.status(HttpStatus.OK).body(crawldbWebpages.toString());
        }


    }
}