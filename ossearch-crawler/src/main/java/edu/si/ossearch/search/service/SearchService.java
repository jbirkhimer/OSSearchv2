package edu.si.ossearch.search.service;

import edu.si.ossearch.OSSearchException;
import edu.si.ossearch.search.beans.request.Paging;
import edu.si.ossearch.search.beans.request.Query;

import javax.validation.Valid;

/**
 * @author jbirkhimer
 */
public interface SearchService {

    //SearchResponseDTO search(SearchRequestDTO searchRequest);

    Object search(Query query, @Valid Paging paging, Boolean edan, Boolean edanDebug) throws Exception, OSSearchException;

//    void parseSpecialTerms(SolrQuery solrQuery, String query);
//
//    void processSort(SolrQuery solrQuery, String substringAfter) throws UnsupportedEncodingException;
}
