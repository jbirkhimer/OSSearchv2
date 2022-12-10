package edu.si.ossearch.security.repository;

import edu.si.ossearch.security.models.Privilege;
import edu.si.ossearch.security.models.PrivilegeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@Tag(name = "Privilege", description = "PrivilegeRepository")
@RepositoryRestResource
@SecurityRequirement(name = "bearerAuth")
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Optional<Privilege> findByName(String name);

    @Override
    void delete(Privilege privilege);

}
