package edu.si.ossearch.utils.controller;

import edu.si.ossearch.dao.repository.CollectionRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/utils")
@Tag(description = "Utils", name = "Utils")
@SecurityRequirement(name = "bearerAuth")
public class SolrUtilsController {

    @Value(value = "${spring.data.solr.host}")
    @NonNull
    String solrUrl;
    @Value(value = "${spring.data.solr.collection}")
    @NonNull
    String solrCollection;

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    private SolrClient solrClient;

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

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFacetField("collectionID");
        query.setFacetMinCount(1);
        query.setFacetLimit(-1);
        query.setRows(0);

        QueryResponse rsp = solrClient.query(solrCollection, query);

        List<FacetField> collectionCounts = rsp.getFacetFields();

        JSONArray collectionCountsJson = new JSONArray();

        collectionCounts.get(0).getValues().forEach(count -> {
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
        });


//        JSONObject index = rsp.getJSONObject("status").getJSONObject(solrCollection).getJSONObject("index");


        return ResponseEntity.status(HttpStatus.OK).body(collectionCountsJson.toString());
    }

}