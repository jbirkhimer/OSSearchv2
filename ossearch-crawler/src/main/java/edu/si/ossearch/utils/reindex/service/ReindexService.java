package edu.si.ossearch.utils.reindex.service;

import edu.si.ossearch.utils.reindex.request.ReindexRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author jbirkhimer
 */
public interface ReindexService {

    ResponseEntity<?> batchReindex(List<ReindexRequest> reindexRequestList);
}
