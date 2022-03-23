package edu.si.ossearch.scheduler.repository;

import edu.si.ossearch.scheduler.entity.NutchStepArgs;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "NutchStepArgs", description = "NutchStepArgsRepository")
//@Repository
@RepositoryRestResource(collectionResourceRel = "nutchstepargs", path = "nutchstepargs", exported = false)
@SecurityRequirement(name = "bearerAuth")
public interface NutchStepArgsRepository extends JpaRepository<NutchStepArgs, Long> {
}