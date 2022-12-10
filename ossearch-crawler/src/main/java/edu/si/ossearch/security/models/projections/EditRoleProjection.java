package edu.si.ossearch.security.models.projections;

import edu.si.ossearch.security.models.Role;
import edu.si.ossearch.security.models.RoleType;
import edu.si.ossearch.security.models.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

/**
 * @author jbirkhimer
 */
@Projection(name = "role", types = { Role.class })
public interface EditRoleProjection {

    String getName();

    List<PrivilegesInfoProjection> getPrivileges();

    interface PrivilegesInfoProjection {
        String getName();
    }
}
