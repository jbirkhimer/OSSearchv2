package edu.si.ossearch.collection.service;

import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.collection.entity.DynamicNavigation;

import java.util.List;
import java.util.Optional;

/**
 * @author jbirkhimer
 */
public interface CollectionService {

    List<Collection> getCollection();

    Optional<Collection> getCollectionById(Long id);

    Collection getCollectionByName(String name);

    Collection createCollection(Collection collection);

    Collection updateCollection();
}
