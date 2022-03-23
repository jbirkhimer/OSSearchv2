package edu.si.ossearch.security.jwt.repository;

import edu.si.ossearch.security.jwt.models.Role;
import edu.si.ossearch.security.jwt.models.RoleType;
import edu.si.ossearch.security.jwt.models.projections.EditRoleProjection;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@Tag(name = "Role", description = "RoleRepository")
@RepositoryRestResource//(excerptProjection = EditRoleProjection.class)
@SecurityRequirement(name = "bearerAuth")
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
