package edu.si.ossearch.collection.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.*;
import edu.si.ossearch.collection.entity.converters.*;
import edu.si.ossearch.security.models.projections.UserIdNameProjection;
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

        String getImgUrlForMatch();

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

//        Set<RegexUrlFilters> getRegexUrlFilters();

        Set<UrlExclusionPatternFormData> getUrlExclusionPatterns();

        Set<UrlNormalizerPatternFormData> getUrlNormalizerPatterns();

        Boolean getUseSitemap();

        Set<String> getSitemapUrls();

        Set<String> getIncludeSitemapUrls();

        Set<String> getExcludeSitemapUrls();

        String getCrawlCronSchedule();

        String getCronEditorData();

        //@Projection(name = "UrlExclusionPatternFormData", types = { UrlExclusionPattern.class })
        interface UrlExclusionPatternFormData {

            int getOrderId();

            String getExpression();

            @JsonSerialize(converter = UrlExclusionPatternTypeConverter.class)
            UrlExclusionPattern.Type getType();

            @JsonSerialize(converter = UrlExclusionPatternScopeConverter.class)
            UrlExclusionPattern.Scope getScope();

            Boolean getIgnoreCase();
        }
        interface UrlNormalizerPatternFormData {
            String getPattern();

            String getSubstitution();

            @JsonSerialize(converter = UrlNormalizerPatternScopeConverter.class)
            URLNormalizerPattern.Scope getScope();
        }
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getDateCreated();
}
