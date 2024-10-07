package edu.si.ossearch.collection.repository;

import edu.si.ossearch.collection.entity.Keymatch;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@Tag(name = "Keymatch", description = "KeymatchRepository")
@RepositoryRestResource(collectionResourceRel = "keymatch", path = "keymatch")
@SecurityRequirement(name = "bearerAuth")
public interface KeymatchRepository extends JpaRepository<Keymatch, Long> {
    @RestResource(path = "byCollection")
    Page<Keymatch> findByCollection_Name(@Param("collectionName") String collectionName, Pageable pageable);

    @RestResource(path = "byCollectionAndSearchTerm")
    @Query("SELECT k FROM Keymatch k WHERE k.collection.name = :collectionName AND " +
            "(LOWER(k.searchTerm) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(k.titleForMatch) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(k.urlForMatch) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(k.imgUrlForMatch) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(k.keymatchType) LIKE LOWER(CONCAT('%', :searchTerm, '%')))"
    )
    Page<Keymatch> findByCollection_NameAndMultipleColumnsContainingIgnoreCase(
            @Param("collectionName") String collectionName,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}