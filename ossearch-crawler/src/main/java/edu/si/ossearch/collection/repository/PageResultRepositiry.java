package edu.si.ossearch.collection.repository;

import edu.si.ossearch.collection.entity.PageResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author jbirkhimer
 */
@Tag(name = "PageResult", description = "PageResultRepositiry")
@RepositoryRestResource(collectionResourceRel = "pageResult", path = "pageResult")
@SecurityRequirement(name = "bearerAuth")
public interface PageResultRepositiry extends JpaRepository<PageResult, Long> {
}
