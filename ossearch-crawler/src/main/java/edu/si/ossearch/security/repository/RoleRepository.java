package edu.si.ossearch.security.repository;

import edu.si.ossearch.security.models.Role;
import edu.si.ossearch.security.models.RoleType;
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
