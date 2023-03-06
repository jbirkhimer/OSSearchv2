package edu.si.ossearch.collection.entity.projections;

import edu.si.ossearch.collection.entity.Collection;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

/**
 * @author jbirkhimer
 */
@Projection(name = "overlappingCollections", types = { Collection.class })
public interface OverlappingCollections {
    long getId();

    String getName();

    Set<CollectionIdNameInfo> getIncludedCollections();

    Set<CollectionIdNameInfo> getPartOfCollections();
}
