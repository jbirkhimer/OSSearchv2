package edu.si.ossearch.dao.service;

import edu.si.ossearch.dao.entity.Collection;

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
