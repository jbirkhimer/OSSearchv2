package edu.si.ossearch.security.jwt.repository;

import edu.si.ossearch.security.jwt.models.Privilege;
import edu.si.ossearch.security.jwt.models.PrivilegeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Tag(name = "Privilege", description = "PrivilegeRepository")
@RepositoryRestResource
@SecurityRequirement(name = "bearerAuth")
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Optional<Privilege> findByName(String name);

    @Override
    void delete(Privilege privilege);

}