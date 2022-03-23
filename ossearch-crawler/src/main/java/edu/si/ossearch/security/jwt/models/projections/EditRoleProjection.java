package edu.si.ossearch.security.jwt.models.projections;

import edu.si.ossearch.security.jwt.models.Role;
import edu.si.ossearch.security.jwt.models.RoleType;
import edu.si.ossearch.security.jwt.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
