package edu.si.ossearch.reports.service.impl;

import edu.si.ossearch.nutch.entity.Webpage;
import edu.si.ossearch.nutch.entity.projections.WebpageUrlStatusCounts;
import edu.si.ossearch.nutch.repository.WebpageRepository;
import edu.si.ossearch.reports.service.ExportService;
import edu.si.ossearch.reports.service.ReportsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class ReportsServiceImpl implements ReportsService {

    /*@Autowired
    private JdbcTemplate jdbcTemplate;*/

    @Value(value = "${spring.data.solr.collection}")
    @NonNull
    String solrCollection;

    @Autowired
    @Qualifier("master")
    private SolrClient solrClient;

    @Autowired
    private WebpageRepository webpageRepository;

    @Autowired
    private ExportService exportService;

    @Override
    public JSONObject solrReport(Integer collectionId, String search, String sort, Integer start, Integer rows) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();

        if (search.trim().isEmpty()) {
            query.setQuery("*:*");
        } else {
            // Use edismax query parser
            query.setParam("defType", "edismax");

            // Separate URL-like queries from regular text queries
            String urlLikePattern = "^(https?://|www\\.).*";
            boolean isUrlLikeQuery = search.matches(urlLikePattern);

            if (isUrlLikeQuery) {
                // For URL-like queries, focus on the URL field with wildcards
                query.setParam("qf", "url^10");
                query.setQuery("url:*" + ClientUtils.escapeQueryChars(search) + "*");
            } else {

                // Set fields to search, with boosting for url and title
                query.setParam("qf", "url^10 title^7");

                // Enable wildcard searches
                query.setParam("q.op", "OR");
                query.setParam("mm", "2<-1 5<-2 6<90%");

                // Enable fuzzy search
                String fuzzySearch = search.replaceAll("\\s+", "~ ") + "~";
                query.setQuery(fuzzySearch);

                // Phrase boosting
                query.setParam("pf", "url^10 title^7");
                query.setParam("ps", "3");

                // Exact matching
                query.setParam("pf2", "url^50 title^20");
                query.setParam("ps2", "0");
            }
        }

        query.addFilterQuery("collectionID:" + collectionId);
        query.setFields("id,url,title,type");

        for (String field : sort.split(",")) {
            String[] sortField = field.split(" ");
            query.addSort(sortField[0], sortField[1].equalsIgnoreCase("asc") ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc);
        }

        query.setStart(start);
        if (rows == -1) {
            query.setRows(0);
            QueryResponse rsp = solrClient.query(solrCollection, query);
            long numfound = rsp.getResults().getNumFound();
            query.setRows(Math.toIntExact(numfound));
        } else {
            query.setRows(rows);
        }

        QueryResponse rsp = solrClient.query(solrCollection, query);

        long numfound = rsp.getResults().getNumFound();

        JSONArray data = new JSONObject(rsp.jsonStr()).getJSONArray("response");

        JSONObject answer = new JSONObject();
        answer.put("data", data);
        answer.put("recordsTotal", numfound);
        answer.put("recordsFiltered", numfound);

        return answer;
    }

    @Override
    public JSONObject solrUrlReport(Integer collectionId, String url) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();

        query.setQuery("url:\""+url+"\"");
        query.addFilterQuery("collectionID:"+collectionId);
        query.addField("*");
        query.addField("score");
        query.setRows(1);

        QueryResponse rsp = solrClient.query(solrCollection, query);

        long numfound = rsp.getResults().getNumFound();

        log.info("solr result: {}", rsp.toString());
        JSONArray data = new JSONObject(rsp.jsonStr()).getJSONArray("response");

        JSONObject answer = new JSONObject();
        answer.put("data", data);
        answer.put("recordsTotal", numfound);
        answer.put("recordsFiltered", numfound);

        return answer;
    }

    @Override
    public Optional<List<WebpageUrlStatusCounts>> crawldbStats(Integer collectionId, String search, String statusName) {

        Optional<List<WebpageUrlStatusCounts>> stats = webpageRepository.getCrawldbStats(collectionId, search, statusName);

        return stats;
    }

    @Override
    public JSONObject crawldbWebpages(Integer collectionId, String search, String statusName, List<String> sortList, Integer page, Integer rows, Boolean export) {

        JSONObject answer = new JSONObject();

        List<Sort.Order> orders = new ArrayList<>();
        if (sortList.get(0).contains(",")) {
            sortList.forEach(sort -> {
                String[] param = sort.split(",");
                Sort.Direction dir = param[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                orders.add(new Sort.Order(dir, param[0]));
            });
        } else {
            Sort.Direction dir = sortList.get(1).equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(dir, sortList.get(0)));
        }

        Sort sort = Sort.by(orders);

        if (export) {
            Optional<List<Webpage>> crawldbReport = webpageRepository.findByWebPagesByCollectionId(collectionId, search, statusName, sort);

            //JSONArray data = new JSONArray(crawldbReport.get());
            JSONArray data = new JSONArray();
            crawldbReport.get().forEach(webpage -> {
                JSONObject row = new JSONObject(webpage);
                row.remove("crawlDb");
                row.remove("id");
                data.put(row);
            });

            answer.put("data", data);

        } else {
            Pageable pageableRequest = PageRequest.of(page, rows, sort);

            Optional<Page<Webpage>> webpages = webpageRepository.findByWebPagesByCollectionId(collectionId, search, statusName, pageableRequest);

            JSONArray data = new JSONArray(webpages.get());
            answer.put("data", data);
            answer.put("recordsTotal", data.length());
            answer.put("recordsFiltered", data.length());
        }

        return answer;
    }

    @Override
    public ByteArrayInputStream export(List<Map<String, Object>> data, String type, String name) throws IOException {
        if (type == null || type.equalsIgnoreCase("csv")) {
            return exportService.toCSV(data);
        } else if (type.equalsIgnoreCase("xlsx") | type.equalsIgnoreCase("xls")) {
            return exportService.toExcel(data, name);
        } else if (type.equalsIgnoreCase("pdf")) {
            return exportService.toPdf(data);
        }
        return null;
    }
}
