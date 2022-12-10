package edu.si.ossearch.security.models.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.collection.entity.projections.CollectionIdNameInfo;
import edu.si.ossearch.security.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.Set;

/**
 * @author jbirkhimer
 */
@Projection(name = "userInfo", types = { User.class })
public interface EditUserProjection {

    long getId();
    String getUsername();
    @Value("#{target.firstName} #{target.lastName}")
    String getFullName();
    String getFirstName();
    String getLastName();
    String getEmail();
    Boolean getEnabled();
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getDateCreated();
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getDateUpdated();

    Set<EditRoleProjection> getRoles();
    Set<String> getRoleList();

    Set<CollectionIdNameInfo> getCollections();
}
