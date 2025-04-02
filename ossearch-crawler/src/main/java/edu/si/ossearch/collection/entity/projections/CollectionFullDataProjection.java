package edu.si.ossearch.collection.entity.projections;

import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.collection.entity.CrawlConfig;
import edu.si.ossearch.collection.entity.DynamicNavigation;
import edu.si.ossearch.collection.entity.Keymatch;
import edu.si.ossearch.collection.entity.PageResult;
import edu.si.ossearch.security.models.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.Set;

/**
 * A projection that includes all Collection data, including lazily loaded collections
 */
@Projection(name = "fullData", types = { Collection.class })
public interface CollectionFullDataProjection {
    long getId();
    String getName();
    String getDescription();
    String getSiteUrl();
    Collection.ResponseType getResponseType();
    Integer getResultsPerPage();
    Integer getResultLimit();
    Boolean getExcludeDuplicateResults();
    
    Set<String> getIncludeFields();
    Set<String> getKeywords();
    Set<Keymatch> getKeymatches();
    Boolean getUseFacets();
    Set<DynamicNavigation> getDynamicNavigations();
    Set<PageResult> getPageResults();
    
    Set<Collection> getIncludedCollections();
    Set<Collection> getPartOfCollections();
    
    CrawlConfig getCrawlConfig();
    
    User getOwner();
    Set<User> getUsers();
    
    Date getDateCreated();
    Date getDateUpdated();
}
