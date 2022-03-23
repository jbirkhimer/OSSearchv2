package edu.si.ossearch.security.jwt.repository;

import edu.si.ossearch.security.jwt.models.User;
import edu.si.ossearch.security.jwt.models.projections.EditUserProjection;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@Tag(name = "User", description = "UserRepository")
@RepositoryRestResource//(excerptProjection = EditUserProjection.class)
@SecurityRequirement(name = "bearerAuth")
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
