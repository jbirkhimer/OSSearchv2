package edu.si.ossearch.dao.repository;

import edu.si.ossearch.dao.entity.DynamicNavigation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "DynamicNavigation", description = "DynamicNavigationRepository")
//@RepositoryRestResource(collectionResourceRel = "dynamicNavigation", path = "dynamic-navigation", exported = false)
@RepositoryRestResource(collectionResourceRel = "dynamicNavigation", path = "dynamic-navigation")
@SecurityRequirement(name = "bearerAuth")
public interface DynamicNavigationRepository extends JpaRepository<DynamicNavigation, Long> {
}