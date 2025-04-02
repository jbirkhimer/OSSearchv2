package edu.si.ossearch.collection.controller;

import edu.si.ossearch.collection.repository.CollectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Custom controller to handle Collection repository DELETE operation
 * without LazyInitializationException
 */
@Slf4j
@RepositoryRestController
public class CustomCollectionRepositoryRestController {

    @Autowired
    private CollectionRepository collectionRepository;

    /**
     * Custom DELETE handler for collections to avoid LazyInitializationException
     * This overrides the default Spring Data REST DELETE endpoint
     */
    @DeleteMapping("/collection/{id}")
    @Transactional
    public ResponseEntity<?> deleteCollection(@PathVariable("id") Long id) {
        log.info("Deleting collection with ID: {}", id);
        
        // Check if collection exists
        if (!collectionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Delete the collection
        collectionRepository.deleteById(id);
        
        // Return success response without trying to serialize the deleted entity
        return ResponseEntity.noContent().build();
    }
}
