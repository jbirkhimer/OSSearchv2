package edu.si.ossearch.collection.repository;

import edu.si.ossearch.collection.entity.EDANFieldMapping;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "EDANFieldMapping", description = "EDANFieldMappingRepository")
@RepositoryRestResource(collectionResourceRel = "edanFieldMapping", path = "edanFieldMapping")
@SecurityRequirement(name = "bearerAuth")
public interface EDANFieldMappingRepository extends JpaRepository<EDANFieldMapping, Long> {
}
