package edu.si.ossearch.reports.controller;

import edu.si.ossearch.collection.repository.CollectionRepository;
import edu.si.ossearch.search.service.SearchMetaTagService;
import edu.si.ossearch.security.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jbirkhimer
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/utils")
@Tag(description = "Utils", name = "Utils")
@SecurityRequirement(name = "bearerAuth")
public class SolrReportController {

    @Value(value = "${spring.data.solr.host}")
    @NonNull
    String solrUrl;
    @Value(value = "${spring.data.solr.collection}")
    @NonNull
    String solrCollection;

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    @Qualifier("master")
    private SolrClient solrClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SearchMetaTagService searchMetaTagService;

    @Operation(summary = "Solr Meta Fields", tags = " Meta Fields", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/solr/metaFields")
    public ResponseEntity<List<String>> getSolrMetaFields() {

        List<String> answer = searchMetaTagService
                .getMetaTags()
                .stream()
                .map(metaField -> metaField.replace("meta_", "").replace("__", " ").replace("_", ":"))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(answer);
    }

    @Operation(summary = "Solr Record Count", tags = "Solr Counts", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/solr/count")
    public ResponseEntity<String> getSolrRecordCount() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(solrUrl + "/admin/cores?action=STATUS&wt=json&core="+solrCollection, String.class);

        JSONObject rsp = new JSONObject(response.getBody());

        JSONObject index = rsp.getJSONObject("status").getJSONObject(solrCollection).getJSONObject("index");


        return ResponseEntity.status(response.getStatusCode()).body(index.toString());
    }

    @Operation(summary = "Solr Collection Record Counts", tags = "Solr Counts", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/solr/collection_counts")
    public ResponseEntity<String> getSolrCollectionCounts() throws IOException, SolrServerException {

        JSONObject answer = new JSONObject();
        JSONArray collectionCountsJson = new JSONArray();

        answer.put("totalCount", collectionCountsJson.length());
        answer.put("data", collectionCountsJson);

//        List<Collection> userCollections = collectionRepository.findAll();

        List<CollectionRepository.CollectionIdNameInfoTest> userCollections = collectionRepository.findAllCollectionsByOwnerAndUsers();

        if (!userCollections.isEmpty()) {
            SolrQuery totalCountQquery = new SolrQuery();

            List<String> collectionIds = new ArrayList<>();
            userCollections.forEach(collection -> {
                collectionIds.add(String.valueOf(collection.getId()));
            });

            totalCountQquery.setQuery("*:*");
            totalCountQquery.setFilterQueries("collectionID:(" + String.join(" ", collectionIds) + ")");
            totalCountQquery.setRows(0);

            QueryResponse totalCountRsp = solrClient.query(solrCollection, totalCountQquery);

            answer.put("totalCount", String.valueOf(totalCountRsp.getResults().getNumFound()));

            SolrQuery query = new SolrQuery();
            query.setQuery("*:*");
            query.addFacetField("collectionID");
            query.setFacetMinCount(1);
            query.setFacetLimit(-1);
            query.setRows(0);

            QueryResponse rsp = solrClient.query(solrCollection, query);

            List<FacetField> collectionCounts = rsp.getFacetFields();

            collectionCounts.get(0).getValues().forEach(count -> {
                JSONObject collectionCount = new JSONObject();
                String collectionID = count.getName();
                long recordCount = count.getCount();

                Optional<CollectionRepository.CollectionIdNameInfoTest> matchingObject = userCollections.stream().
                        filter(p -> String.valueOf(p.getId()).equals(collectionID)).
                        findFirst();

                if (matchingObject.isPresent()) {
                    String collectionName = "unknownName id: " + collectionID;
                    collectionName = matchingObject.get().getName();

                    collectionCount.put("id", collectionID);
                    collectionCount.put("name", collectionName);
                    collectionCount.put("count", recordCount);

                    collectionCountsJson.put(collectionCount);
                }

            });
        }

        /*collectionCounts.get(0).getValues().forEach(count -> {
            JSONObject collectionCount = new JSONObject();
            String collectionID = count.getName();
            long recordCount = count.getCount();

            String collectionName = "unknownName id: " + collectionID;
            try {
                collectionName = collectionRepository
                        .findCollectionById(Long.parseLong(collectionID))
                        .orElse("nameNotFound id: " + collectionID);
            } catch (Exception e) {

            }

            collectionCount.put("id", collectionID);
            collectionCount.put("name", collectionName);
            collectionCount.put("count", recordCount);

            collectionCountsJson.put(collectionCount);
        });*/

        answer.put("data", collectionCountsJson);

        return ResponseEntity.status(HttpStatus.OK).body(answer.toString());
    }

}
