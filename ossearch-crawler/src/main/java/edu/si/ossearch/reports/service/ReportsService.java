package edu.si.ossearch.reports.service;

import edu.si.ossearch.nutch.entity.projections.WebpageUrlStatusCounts;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author jbirkhimer
 */
public interface ReportsService {

    /*Integer getRecordsTotal(Long collectionId, long startDate, long endDate, boolean isCounts);

    Integer getRecordsFiltered(Long collectionId, long startDate, long endDate, String searchTerm, boolean isCounts);

    Optional<List<Map<String, Object>>> getKeywordsByCollectionId(Long collectionId, Long startDate, Long endDate, String sortColumn, String sortDir, int start, int rows, String search, boolean isCounts);*/

    JSONObject solrReport(Integer collectionId, String search, String sort, Integer start, Integer rows) throws SolrServerException, IOException;

    JSONObject solrUrlReport(Integer collectionId, String url) throws SolrServerException, IOException;

    Optional<List<WebpageUrlStatusCounts>> crawldbStats(Integer collectionId, String search, String statusName);

    JSONObject crawldbWebpages(Integer collectionId, String search, String statusName, List<String> sort, Integer page, Integer rows, Boolean export);

    ByteArrayInputStream export(List<Map<String, Object>> data, String type, String name) throws IOException;
}
