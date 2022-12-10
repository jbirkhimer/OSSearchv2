package edu.si.ossearch.edan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.solr.common.SolrDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
@Data
@NoArgsConstructor
@ToString
public class EdanResponse {
    private List<SolrDocument> rows = new ArrayList<>();
    private Object facetQueries;
    private Map<String, List<List<String>>> facets = new HashMap<>();
    private Long start;
    private Long rowCount;
}
