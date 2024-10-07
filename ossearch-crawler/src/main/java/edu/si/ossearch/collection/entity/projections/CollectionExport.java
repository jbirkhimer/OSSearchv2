package edu.si.ossearch.collection.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.si.ossearch.collection.entity.*;
import edu.si.ossearch.collection.entity.converters.*;
import edu.si.ossearch.security.models.projections.EditRoleProjection;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.Set;

/**
 * Projection for {@link Collection}
 */
@Projection(name = "collectionExport", types = { Collection.class })
public interface CollectionExport {
    //long getId();

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getDateCreated();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getDateUpdated();

    Set<KeymatchInfo> getKeymatches();

    Set<DynamicNavigationInfo> getDynamicNavigations();

    Set<PageResultInfo> getPageResults();

    Set<CollectionInfo> getIncludedCollections();

    Set<CollectionInfo> getPartOfCollections();

    CrawlConfigInfo getCrawlConfig();

    Set<UserInfo> getUsers();

    UserInfo getOwner();

    /**
     * Projection for {@link Keymatch}
     */
    interface KeymatchInfo {
        //long getId();

        String getTitleForMatch();

        String getSearchTerm();

        @JsonSerialize(converter = KeymatchTypeConverter.class)
        Keymatch.KeymatchType getKeymatchType();

        String getUrlForMatch();

        String getImgUrlForMatch();

        @JsonSerialize(converter = CreationTypeConverter.class)
        Keymatch.CreationType getCreationType();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getCreatedDate();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getUpdatedDate();
    }

    /**
     * Projection for {@link DynamicNavigation}
     */
    interface DynamicNavigationInfo {
        //long getId();

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

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getCreatedDate();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getUpdatedDate();
    }

    /**
     * Projection for {@link PageResult}
     */
    interface PageResultInfo {
        //Long getId();

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

    /**
     * Projection for {@link Collection}
     */
    interface CollectionInfo {
        //long getId();
        String getName();
    }

    /**
     * Projection for {@link CrawlConfig}
     */
    interface CrawlConfigInfo {
        //Long getId();

        String getCrawlDbPath();

        String getCrawlSeedPath();

        Set<String> getSeedUrls();

        Set<String> getIncludeSiteUrls();

        Set<String> getExcludeSiteUrls();

        Boolean getUseSitemap();

        Set<String> getSitemapUrls();

        Set<String> getIncludeSitemapUrls();

        Set<String> getExcludeSitemapUrls();

        String getCrawlCronSchedule();

        String getCronEditorData();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getCreatedDate();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date getUpdatedDate();

        Set<URLNormalizerPatternInfo> getUrlNormalizerPatterns();

        Set<UrlExclusionPatternInfo> getUrlExclusionPatterns();

        /**
         * Projection for {@link URLNormalizerPattern}
         */
        interface URLNormalizerPatternInfo {
            String getPattern();

            String getSubstitution();

            @JsonSerialize(converter = UrlNormalizerPatternScopeConverter.class)
            URLNormalizerPattern.Scope getScope();
        }

        /**
         * Projection for {@link UrlExclusionPattern}
         */
        interface UrlExclusionPatternInfo {
            int getOrderId();

            String getExpression();

            @JsonSerialize(converter = UrlExclusionPatternTypeConverter.class)
            UrlExclusionPattern.Type getType();

            @JsonSerialize(converter = UrlExclusionPatternScopeConverter.class)
            UrlExclusionPattern.Scope getScope();

            Boolean getIgnoreCase();
        }
    }

    /**
     * Projection for {@link edu.si.ossearch.security.models.User}
     */
    interface UserInfo {
        //Long getId();

        String getUsername();

        String getEmail();

        String getFirstName();

        String getLastName();

        //Set<String> getRoleList();

        //Set<EditRoleProjection> getRoles();
    }
}