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
            search = "*:*";
        } else if (!search.trim().equalsIgnoreCase("*:*")) {
            search = "url:\"" + search + "\" OR title:\"" + search + "\" OR type:\"" + search + "\"";
        }
        query.setQuery(search);
        query.addFilterQuery("collectionID:"+collectionId);
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

        /*while (!done) {
            query.setStart(cursorMark);
            rsp = solrClient.query("search", solrQuery);
            int nextCursorMark = cursorMark + solrQuery.getRows();
            for (SolrDocument d : rsp.getResults()) {

                JSONObject jo = new JSONObject(d.getFieldValue("content").toString());
                JSONObject descriptiveNonRepeating = jo.getJSONObject("descriptiveNonRepeating");
                JSONObject freetext = jo.getJSONObject("freetext");
                String recordId = descriptiveNonRepeating.getString("record_ID");

                String channel = null;
                Iterator<Object> dsIt = freetext.getJSONArray("dataSource").iterator();
                for (Iterator<Object> ds = dsIt; ds.hasNext(); ) {
                    JSONObject o = (JSONObject) ds.next();
                    if (o.getString("label").equals("YouTube Channel")) {
                        channel = o.getString("content");
                    }
                }

                String title = d.getFieldValue("title").toString();
                Iterator<Object> nI = freetext.getJSONArray("notes").iterator();
                int views = 0;
                for (Iterator<Object> it = nI; it.hasNext(); ) {
                    JSONObject o = (JSONObject) it.next();
                    String label = String.valueOf(o.get("label"));
                    if (label.equalsIgnoreCase("views")) {
                        views = Integer.valueOf(String.valueOf(o.get("content")).replaceAll(",", ""));
                        break;
                    }
                }

                youTubeVideoList.add(new YouTubeVideo(recordId, channel, title, views));

                log.info("doc {}/{}: {}, {}, {}, {}", count++, numfound, recordId, channel, title, views);

            }
            if (nextCursorMark >= numfound) {
                done = true;
            }
            cursorMark = nextCursorMark;

        }*/

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
