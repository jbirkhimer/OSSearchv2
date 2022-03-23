package edu.si.ossearch.dao.entity.projections;

import edu.si.ossearch.dao.entity.Collection;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

/**
 * @author jbirkhimer
 */
@Projection(name = "collectionIdNameInfo", types = { Collection.class })
public interface CollectionIdNameInfo {
    long getId();

    String getName();
}
