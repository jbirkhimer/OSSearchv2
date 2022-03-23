package edu.si.ossearch.dao.controller;

import edu.si.ossearch.dao.dto.CollectionDtoFlat;
import edu.si.ossearch.dao.entity.Collection;
import edu.si.ossearch.dao.entity.projections.CollectionFormData;
import edu.si.ossearch.dao.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author jbirkhimer
 */
@Slf4j
@RestController
@RequestMapping("/api/collection2")
//@Api(value = "Collection Manager", tags = "Collection Manager")
@Tag(description = "Collection Manager", name = "Collection Manager")
@SecurityRequirement(name = "bearerAuth")
public class CollectionManagerController {

    @Autowired
    CollectionService collectionService;

    @Autowired
    EntityLinks entityLinks;

//    @ApiOperation(value = "list collections", response = String.class)
    @Operation(summary = "list collections", responses = {@ApiResponse(content = @Content(mediaType = "text/plain"))})
    @GetMapping
    public List<Collection> getCollections() {
        return collectionService.getCollection();
    }

//    @ApiOperation(value = "get collection id", responses = String.class)
    @Operation(summary = "get collection id", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/{id:\\d+}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable(name = "id") Long id) {
        Optional<Collection> optionalCollection = collectionService.getCollectionById(id);
        if (!optionalCollection.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(optionalCollection.get());
    }

//    @ApiOperation(value = "get collection name", response = String.class)
    @Operation(summary = "get collection name", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @GetMapping(value = "/{name:(?!\\d+$)\\S*$}")
    public Collection getCollectionByName(@PathVariable(name = "name") String name) {
        return collectionService.getCollectionByName(name);
    }

//    @ApiOperation(value = "create collection", response = String.class)
    @Operation(summary = "create collection", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PostMapping
    public ResponseEntity<CollectionFormData> createCollection(@RequestBody Collection collectionFormData) {
        log.info("create collection: {}", collectionFormData);

        Collection savedCollection = collectionService.createCollection(collectionFormData);

        //URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedCollection.getId()).toUri();
        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
        CollectionFormData collectionFormData1 = projectionFactory.createProjection(CollectionFormData.class, savedCollection);
        Link link = entityLinks.linkToItemResource(Collection.class, savedCollection.getId()).expand();
        URI location = URI.create(link.getHref());

        return ResponseEntity.created(location).body(collectionFormData1);
    }

//    @ApiOperation(value = "update collection by id", response = String.class)
    @Operation(summary = "update collection by id", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PutMapping(value = "/{id:\\d+}")
    public Collection updateCollectionId(@PathVariable Integer id) {
        return collectionService.updateCollection();
    }

//    @ApiOperation(value = "update collection by name", response = String.class)
    @Operation(summary = "update collection by name", responses = {@ApiResponse(content = @Content(mediaType = "application/json"))})
    @PutMapping(value = "/{name:(?!\\d+$)\\S*$}")
    public Collection updateCollectionName(@PathVariable String name) {
        return collectionService.updateCollection();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e, WebRequest request) {

//        UserResponse response = null;

        if(e instanceof DataIntegrityViolationException){
            log.error("DataIntegrity Violation Exception ::: {}", e);
            DataIntegrityViolationException ex = (DataIntegrityViolationException) e;
//            response = new UserResponse(ErrorCodes.DuplicateMobNo, "This mobile no is already Registered!");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } else if (e instanceof DataRetrievalFailureException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
