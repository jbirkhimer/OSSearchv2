package edu.si.ossearch.search.service;

import edu.si.ossearch.collection.entity.PageResult;
import edu.si.ossearch.search.beans.result.GM;
import edu.si.ossearch.search.beans.request.Paging;
import edu.si.ossearch.search.beans.request.Query;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.List;

/**
 * @author jbirkhimer
 */
public interface SearchPageResultHelperService {

    String generatePageResult(Query query, PageResult pageResult, Paging paging, QueryResponse solrResults, List<GM> keymatches);
}
