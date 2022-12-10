package edu.si.ossearch.collection.service.impl;

import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.collection.repository.CollectionRepository;
import edu.si.ossearch.collection.repository.CrawlConfigRepository;
import edu.si.ossearch.collection.repository.DynamicNavigationRepository;
import edu.si.ossearch.collection.repository.KeymatchRepository;
import edu.si.ossearch.collection.service.CollectionService;
import edu.si.ossearch.security.models.User;
import edu.si.ossearch.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author jbirkhimer
 */
@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    DynamicNavigationRepository dynamicNavigationRepository;

    @Autowired
    KeymatchRepository keymatchRepository;

    @Autowired
    CrawlConfigRepository crawlConfigRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<Collection> getCollection() {
        return collectionRepository.findAll();
    }

    @Override
    public Optional<Collection> getCollectionById(Long id) {
        return collectionRepository.findById(id);
    }

    @Override
    public Collection getCollectionByName(String name) {
        return collectionRepository.getCollectionByName(name);
    }

    @Override
    public Collection createCollection(Collection collection) {
//        Long ownerId = collection.getOwner().getId();
//        User owner = userRepository.getById(ownerId);
//
//        collection

        /*Set<User> userSet = null;
        if (collection.getUsers().size() > 0) {
            userSet = new HashSet<>();
            userSet.addAll(collection.getUsers());

            collection.getUsers().clear();

        }

        Set<Collection> includedCollections = null;
        if (collection.getIncludedCollections().size() > 0) {
            includedCollections = new HashSet<>();
            includedCollections.addAll(collection.getIncludedCollections());

            collection.getIncludedCollections().clear();
        }

        Collection savedCollection = collectionRepository.save(collection);

        for (User user : userSet) {
            Optional<User> optionalUser = userRepository.findById(user.getId());
            if (optionalUser.isPresent()) {
                savedCollection.getUsers().add(optionalUser.get());
                optionalUser.get().getCollections().add(savedCollection);
                userRepository.save(optionalUser.get());
            } else {
                throw new DataRetrievalFailureException("cant add user { id: " + user.getId() + ", name: " + user.getUsername() + "}  not found!");
            }
        }

//        for (Collection includedCollection : includedCollections) {
//            Optional<Collection> optionalCollection = collectionRepository.findById(includedCollection.getId());
//            if (optionalCollection.isPresent()) {
//                savedCollection.getIncludedCollections().add(optionalCollection.get());
//                optionalCollection.get().getPartOfCollections().add(savedCollection);
//            } else {
//                throw new DataRetrievalFailureException("cant include collection { id: " + includedCollection.getId() + ", name: " + includedCollection.getName() + "}  not found!");
//            }
//        }

        return collectionRepository.saveAndFlush(savedCollection);*/


        if (collection.getUsers().size() > 0) {
            Set<User> userSet = new HashSet<>();
            userSet.addAll(collection.getUsers());

            collection.getUsers().clear();

            Collection savedCollection = collectionRepository.save(collection);

            for (User user : userSet) {
                Optional<User> optionalUser = userRepository.findById(user.getId());
                if (optionalUser.isPresent()) {
                    savedCollection.getUsers().add(optionalUser.get());
                    optionalUser.get().getCollections().add(savedCollection);
                    userRepository.save(optionalUser.get());
                } else {
                    throw new DataRetrievalFailureException("cant add user { id: " + user.getId() + ", name: " + user.getUsername() + "}  not found!");
                }
            }
        }

//        if (collection.getIncludedCollections().size() > 0) {
//            Set<Collection> includedCollections = new HashSet<>();
//            includedCollections.addAll(collection.getIncludedCollections());
//
//            for (Collection includedCollection : includedCollections) {
//                Optional<Collection> optionalCollection = collectionRepository.findById(includedCollection.getId());
//                if (optionalCollection.isPresent()) {
//                    collection.getIncludedCollections().remove(includedCollection);
//                    collection.getIncludedCollections().add(optionalCollection.get());
//                } else {
//                    throw new DataRetrievalFailureException("cant include collection { id: " + includedCollection.getId() + ", name: " + includedCollection.getName() + "}  not found!");
//                }
//            }
//        }
        return collectionRepository.saveAndFlush(collection);
    }

    @Override
    public Collection updateCollection() {
        return null;
    }

    private Collection requireOne(Long id) {
        return collectionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }

}
