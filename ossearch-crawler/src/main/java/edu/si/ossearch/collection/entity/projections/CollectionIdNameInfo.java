package edu.si.ossearch.collection.entity.projections;

import edu.si.ossearch.collection.entity.Collection;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author jbirkhimer
 */
@Projection(name = "collectionIdNameInfo", types = { Collection.class })
public interface CollectionIdNameInfo {
    long getId();

    String getName();
}
