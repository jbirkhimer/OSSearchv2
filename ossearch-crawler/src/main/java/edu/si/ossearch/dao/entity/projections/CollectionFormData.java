package edu.si.ossearch.dao.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.dao.entity.*;
import edu.si.ossearch.dao.entity.converters.*;
import edu.si.ossearch.security.jwt.models.projections.UserIdNameProjection;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author jbirkhimer
 */
@Projection(name = "collectionFormData", types = { Collection.class })
public interface CollectionFormData {
    long getId();

    String getName();

    String getDescription();

    String getSiteUrl();

    @JsonSerialize(converter = ResponseTypeConverter.class)
    Collection.ResponseType getResponseType();

    Integer getResultsPerPage();

    Integer getResultLimit();

    Boolean getExcludeDuplicateResults();

    Set<String> getIncludeFields();

    Set<String> getKeywords();

    Boolean getUseFacets();

    Set<KeymatchInfoFormData> getKeymatches();

    Set<DynamicNavigationFormData> getDynamicNavigations();

    Set<CollectionIdNameInfo> getIncludedCollections();

    Set<CollectionIdNameInfo> getPartOfCollections();

    /*String getCrawlDbPath();

    String getCrawlSeedPath();

    Set<String> getSeedUrls();

    Set<String> getIncludeSiteUrls();

    Set<String> getExcludeSiteUrls();

    Set<UrlExclusionPatternFormData> getUrlExclusionPatterns();

    Boolean getUseSitemap();

    String getSitemapUrl();

    Set<String> getIncludeSitemapUrls();

    Set<String> getExcludeSitemapUrls();

    String getCrawlCronSchedule();

    String getCronEditorData();*/

    CrawlConfigFormData getCrawlConfig();

    UserIdNameProjection getOwner();

    List<UserIdNameProjection> getUsers();

    //@Projection(name = "keymatchInfoFormData", types = { Keymatch.class })
    interface KeymatchInfoFormData {
        long getId();

        String getTitleForMatch();

        String getSearchTerm();

        @JsonSerialize(converter = KeymatchTypeConverter.class)
        Keymatch.KeymatchType getKeymatchType();

        String getUrlForMatch();

        @JsonSerialize(converter = CreationTypeConverter.class)
        Keymatch.CreationType getCreationType();
    }

    //@Projection(name = "dynamicNavigationFormData", types = { DynamicNavigation.class })
    interface DynamicNavigationFormData {
        long getId();

        String getDisplayName();

        String getAttributeName();

        @JsonSerialize(converter = SortTypeConverter.class)
        DynamicNavigation.SortType getSort();

        @JsonSerialize(converter = SortByConverter.class)
        DynamicNavigation.SortBy getSortBy();

        Boolean getMultivalue();

        String getDelimiter();

        @JsonSerialize(converter = NavigationTypeConverter.class)
        DynamicNavigation.NavigationType getNavigationType();
    }

    //@Projection(name = "CrawlConfigFormData", types = { CrawlConfig.class })
    interface CrawlConfigFormData {
        long getId();

        String getCrawlDbPath();

        String getCrawlSeedPath();

        Set<String> getSeedUrls();

        Set<String> getIncludeSiteUrls();

        Set<String> getExcludeSiteUrls();

        Set<RegexUrlFilters> getRegexUrlFilters();

        Set<UrlExclusionPatternFormData> getUrlExclusionPatterns();

        Boolean getUseSitemap();

        Set<String> getSitemapUrls();

        Set<String> getIncludeSitemapUrls();

        Set<String> getExcludeSitemapUrls();

        String getCrawlCronSchedule();

        String getCronEditorData();

        //@Projection(name = "UrlExclusionPatternFormData", types = { UrlExclusionPattern.class })
        interface UrlExclusionPatternFormData {

            String getExpression();

            @JsonSerialize(converter = UrlExclusionPatternTypeConverter.class)
            UrlExclusionPattern.Type getType();

            Boolean getIgnoreCase();
        }
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getDateCreated();
}
