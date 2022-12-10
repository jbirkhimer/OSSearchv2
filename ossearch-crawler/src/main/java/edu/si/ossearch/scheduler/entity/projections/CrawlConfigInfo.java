package edu.si.ossearch.scheduler.entity.projections;

import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.collection.entity.RegexUrlFilters;
import edu.si.ossearch.collection.entity.UrlExclusionPattern;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.Set;

/**
 * @author jbirkhimer
 */
@Projection(name = "crawlConfigInfo", types = { CrawlConfigInfo.class })
public interface CrawlConfigInfo {
    String getCrawlDbPath();

    String getCrawlSeedPath();

    Set<String> getSeedUrls();

    Set<String> getIncludeSiteUrls();

    Set<String> getExcludeSiteUrls();

    Boolean isUseSitemap();

    Set<String> getSitemapUrls();

    Set<String> getIncludeSitemapUrls();

    Set<String> getExcludeSitemapUrls();

    String getCrawlCronSchedule();

    String getCronEditorData();

//    Set<RegexUrlFilters> getRegexUrlFilters();

    Set<UrlExclusionPatternInfo> getUrlExclusionPatterns();

//    CollectionInfo getCollection();
    Collection getCollection();

    interface UrlExclusionPatternInfo {
        String getExpression();

        UrlExclusionPattern.Type getType();

        Boolean isIgnoreCase();

        Date getCreatedDate();

        Date getUpdatedDate();
    }

    interface CollectionInfo {
        long getId();
    }
}
