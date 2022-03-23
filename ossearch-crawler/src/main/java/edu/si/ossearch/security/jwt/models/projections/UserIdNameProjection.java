package edu.si.ossearch.security.jwt.models.projections;

import edu.si.ossearch.security.jwt.models.Role;
import edu.si.ossearch.security.jwt.models.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

/**
 * @author jbirkhimer
 */
@Projection(name = "userIdNameEmailRoles", types = { User.class })
public interface UserIdNameProjection {
    long getId();
    String getUsername();
    String getFirstName();
    String getLastName();
    String getEmail();
    Set<String> getRoleList();
}
