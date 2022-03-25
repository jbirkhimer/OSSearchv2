package edu.si.ossearch.dao.repository;

import edu.si.ossearch.dao.entity.Collection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * @author jbirkhimer
 */
@Tag(name = "Collection", description = "CollectionRepository")
@RepositoryRestResource(collectionResourceRel = "collection", path = "collection")
@SecurityRequirement(name = "bearerAuth")
public interface CollectionRepository extends JpaRepository<Collection, Long> {

//    @ApiOperation("Find collection by collection name")
    @Operation(summary = "Find collection by collection name")
    Collection getCollectionByName(@Param("name") String name);

    @Operation(summary = "Find collection name by collection id")
    @Query("select c.name from Collection c where c.id = :id")
    Optional<String> findCollectionById(@Param("id") long id);

    @Operation(summary = "Get collections that this collection is part of by collection id")
    @Query("select c.partOfCollections from Collection c where c.id = :id")
    List<Collection> getPartOfCollectionsByCollectionId(@Param("id") long id);
}
