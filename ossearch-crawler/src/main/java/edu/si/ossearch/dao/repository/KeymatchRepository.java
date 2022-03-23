package edu.si.ossearch.dao.repository;

import edu.si.ossearch.dao.entity.Keymatch;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "Keymatch", description = "KeymatchRepository")
@RepositoryRestResource(collectionResourceRel = "keymatch", path = "keymatch")
@SecurityRequirement(name = "bearerAuth")
public interface KeymatchRepository extends JpaRepository<Keymatch, Long> {
}