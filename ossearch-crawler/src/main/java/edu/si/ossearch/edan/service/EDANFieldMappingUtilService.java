package edu.si.ossearch.edan.service;

import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
public interface EDANFieldMappingUtilService {


    List<Map<String, Object>> loadMetaTagsFromSolr(Long collectionID) throws SolrServerException, IOException;
}
