package edu.si.ossearch.collection.entity.projections;

import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.security.models.projections.UserIdNameProjection;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

/**
 * @author jbirkhimer
 */
@Projection(name = "collectionUsers", types = { Collection.class })
public interface CollectionUsers {
    long getId();

    String getName();

    UserIdNameProjection getOwner();

    List<UserIdNameProjection> getUsers();
}
