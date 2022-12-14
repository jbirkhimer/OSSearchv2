package edu.si.ossearch.collection.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.collection.entity.DynamicNavigation;
import edu.si.ossearch.collection.entity.Keymatch;
import edu.si.ossearch.collection.entity.UrlExclusionPattern;
import edu.si.ossearch.collection.entity.converters.*;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.Set;

/**
 * @author jbirkhimer
 */
@Projection(name = "collectionSearchData", types = { Collection.class })
public interface CollectionSearchData {
    long getId();

    String getName();

//    String getDescription();

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

    Set<PageResultInfo> getPageResults();

    CrawlConfigFormData getCrawlConfig();

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

        Set<String> getExcludeSiteUrls();

//        Set<RegexUrlFilters> getRegexUrlFilters();

        Set<UrlExclusionPatternFormData> getUrlExclusionPatterns();

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
    }

    //@Projection(name = "pageResultInfo", types = { PageResult.class })
    interface PageResultInfo {
        Long getId();

        String getLogoPath();

        String getName();

        String getNameNoSpace();

        String getStylesheet();

        String getLinkColor();

        String getHeader();

        String getFooter();

        String getButtonText();

        Integer getBoxMaxChar();

        Set<String> getIncludeField();

        Boolean getUseSearchButton();

        String getHead();

        String getFullHtml();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getDateCreated();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getDateUpdated();
    }
}
