package edu.si.ossearch.edan.service.impl;

import edu.si.ossearch.edan.service.EDANFieldMappingUtilService;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
public class EDANFieldMappingUtilServiceImpl implements EDANFieldMappingUtilService {

    @Autowired
    @Qualifier("master")
    SolrClient solrClient;

    @Value(value = "${spring.data.solr.host}")
    @NonNull
    String solrUrl;
    @Value(value = "${spring.data.solr.collection}")
    @NonNull
    String solrCollection;

    @Override
    public List<Map<String, Object>> loadMetaTagsFromSolr(Long collectionID) throws SolrServerException, IOException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.addFilterQuery("collectionID:"+collectionID);
        solrQuery.setQuery("*:*");
        solrQuery.setRows(0);
        solrQuery.addFacetField("meta__all_metatags");
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetLimit(-1);

        QueryResponse rsp = solrClient.query(solrCollection, solrQuery);
        Map<String, Long> metaFields = rsp.getFacetField("meta__all_metatags")
                .getValues().stream()
                .collect(Collectors.toMap(FacetField.Count::getName, FacetField.Count::getCount));


        List<Map<String, Object>> answer = rsp.getFacetField("meta__all_metatags")
                .getValues().stream()
                .map(entry -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("fieldName", entry.getName());
                    row.put("count", entry.getCount());
                    return row;
                })
                .collect(Collectors.toList());

        return answer;

        /*LukeRequest lukeRequest = new LukeRequest();
        lukeRequest.setNumTerms(0);

        try {
            LukeResponse lukeResponse = lukeRequest.process(solrClient, solrCollection);
            Map<String, LukeResponse.FieldInfo> fieldInfoMap = lukeResponse.getFieldInfo();

            for (Map.Entry<String, LukeResponse.FieldInfo> entry : fieldInfoMap.entrySet()) {

                String fieldName = entry.getKey();
                LukeResponse.FieldInfo fieldInfo = entry.getValue();

                log.debug("loading from solr field: {}", fieldName);

                fieldName = fieldName.replace("image_", "image:").replace("fb_", "fb:").replace("og_", "og:").replace("twitter_", "twitter:").replace("__", " ");

                allSolrFields.add(fieldName);

                //String metaTag = StringUtils.substringAfter(fieldName, "meta_");

                if ((fieldName.startsWith("meta_") && !fieldName.startsWith("meta__")) || fieldName.equals("topicids")) {
                    log.debug("adding solr meta_ field: {}", fieldName);

                    metaTags.add(fieldName);
                    metaTagMapping.put(fieldName.replace("meta_", ""), entry.getKey());
                    metaTagMapping.put("-"+fieldName.replace("meta_", ""), "-"+entry.getKey());
                } else {
                    solrNonMetaFields.add(fieldName);
                    metaTagMapping.put(fieldName, fieldName);
                    metaTagMapping.put("-"+fieldName, "-"+fieldName);
                }
            }
        } catch (SolrServerException | IOException e) {
            log.error("Problem getting meta tag fields from solr! {}", e.getMessage(), e);
        }*/
    }
}
