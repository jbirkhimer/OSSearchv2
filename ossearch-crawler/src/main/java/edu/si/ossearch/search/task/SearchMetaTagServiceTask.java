package edu.si.ossearch.search.task;

import edu.si.ossearch.search.service.SearchMetaTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Task for refreshing the SearchMetaTagService meta tags
 *
 * @author jbirkhimer
 */
@Slf4j
@Service
public class SearchMetaTagServiceTask {

    @Autowired
    private SearchMetaTagService searchMetaTagService;

    @Scheduled(cron = "@daily")
    public void purgeExpired() {
        Instant now = Instant.now();
        log.warn("Refresh meta tags from solr and nutch-site.xml: ", now);
        searchMetaTagService.init();
        log.warn("Finished refreshing meta tags from solr and nutch-site.xml");
    }
}
