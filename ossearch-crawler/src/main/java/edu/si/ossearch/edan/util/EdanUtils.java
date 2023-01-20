package edu.si.ossearch.edan.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.si.ossearch.edan.EdanResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.json.JSONArray;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jbirkhimer
 */
public class EdanUtils {

    public static MultiValueMap<String, String> getMultiValueMap(SolrQuery solrQuery, Boolean useHighlighting) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("q", solrQuery.getQuery());
        queryParams.add("fl", solrQuery.getFields());
        queryParams.add("rows", String.valueOf(solrQuery.getRows()));
        queryParams.add("start", String.valueOf(solrQuery.getStart()));
        queryParams.add("wt", "json");

        List<String> fqs = Arrays.stream(solrQuery.getFilterQueries()).map(fq -> "p.webpage."+fq.toLowerCase().trim()).collect(Collectors.toList());
        queryParams.add("fqs", new JSONArray(fqs).toString());

        if (solrQuery.getFacetFields() != null) {
            List<String> facetFields = Arrays.stream(solrQuery.getFacetFields()).map(facetField -> "p.webpage." + facetField.toLowerCase().trim()).collect(Collectors.toList());
            queryParams.addAll("facet.field", facetFields);
        }

        queryParams.add("facet", solrQuery.get(FacetParams.FACET));

        if (useHighlighting) {
            queryParams.add("hl", "true");
            queryParams.add("hl.fl", "title,content");
            queryParams.add("hl.fragsize", "120");
            queryParams.add("hl.simple.pre", "<b>");
            queryParams.add("hl.simple.post", "</b>");
        }

        return queryParams;
    }

    public static QueryResponse convertQueryResponse(MultiValueMap<String, String> edanRequest, EdanResponse edanResponse, List<String> fieldsFilter) {
        NamedList<Object> res = new NamedList<>();

        SolrDocumentList solrDocuments = new SolrDocumentList();
        solrDocuments.setStart(edanResponse.getStart());
        solrDocuments.setNumFound(edanResponse.getRowCount());
        edanResponse.getRows().forEach(row -> solrDocuments.add(convertToSolrDoc(row.get("content"), fieldsFilter)));
        res.add("response", solrDocuments);

        NamedList<NamedList<Number>> facet_fields = new NamedList<>();
        edanResponse.getFacets().forEach((facetName, edanFacet) -> {
            NamedList<Number> facetList = new NamedList<>();
            List<List<String>> facets = edanFacet;
            facets.forEach(facet -> facetList.add(facet.get(0), Long.parseLong(facet.get(1))));
            facet_fields.add(facetName.replace("p.webpage.", ""), facetList);
        });
        NamedList<Object> facet_counts = new NamedList<>();
        facet_counts.add("facet_fields", facet_fields);
        res.add("facet_counts", facet_counts);

        NamedList<Object> highlighting = new NamedList<>();
        res.add("highlighting", highlighting);

        NamedList<Object> params = new NamedList<>();
        params.add("start", edanRequest.get("start").get(0));
        params.add("rows", edanRequest.get("rows").get(0));

        NamedList<Object> responseHeader = new NamedList<>();
        responseHeader.add("params", params);
        responseHeader.add("QTime", 0);
        res.add("responseHeader", responseHeader);

        QueryResponse queryResponse = new QueryResponse();
        queryResponse.setResponse(res);
        queryResponse.setElapsedTime(0);

        return queryResponse;
    }

    private static SolrDocument convertToSolrDoc(Object content, List<String> fieldsFilter) {
        Map<String, Object> mapTo = ((Map<String,Object>) content).entrySet().stream()
                .filter(e -> fieldsFilter.get(0).equals("*") ? true : fieldsFilter.contains(e.getKey()))
                .map(e -> {
                    if (e.getValue() instanceof List && ((List<?>) e.getValue()).size() == 1) {
                        return new AbstractMap.SimpleImmutableEntry<>(e.getKey(), ((List<?>) e.getValue()).get(0));
                    } else {
                        return e;
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);

        return new ObjectMapper().convertValue(mapTo, SolrDocument.class);
    }
}
