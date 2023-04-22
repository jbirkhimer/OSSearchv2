package edu.si.ossearch.security.repository.projections;

import edu.si.ossearch.security.models.RefreshToken;
import edu.si.ossearch.security.models.User;
import lombok.Value;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jbirkhimer
 */
@Value
public class LoggedInUserProjectionClass {
    Long id;
    String username;
    String firstName;
    String lastName;
    String email;
    Instant expiryDate;
    Set<String> roleList;

    public LoggedInUserProjectionClass(User user, RefreshToken refreshToken) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.expiryDate = refreshToken.getExpiryDate();

        Set<String> roleList = new HashSet<>();
        user.getRoles().forEach(role -> {
            roleList.add(role.getName());
        });

        this.roleList = roleList;
    }
}
