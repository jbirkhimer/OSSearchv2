package edu.si.ossearch.collection.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.collection.entity.Collection;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Projection(name = "collectionTableData", types = { Collection.class })
public interface CollectionTableData {
    long getId();

    String getName();

    CrawlConfigFormData getCrawlConfig();

    UserIdNameOnlyProjection getOwner();

    List<UserIdNameOnlyProjection> getUsers();

    //@Projection(name = "CrawlConfigFormData", types = { CrawlConfig.class })
    interface CrawlConfigFormData {
        long getId();

        String getCrawlCronSchedule();
    }

    interface UserIdNameOnlyProjection {
        long getId();
        String getUsername();
        String getFirstName();
        String getLastName();
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getDateCreated();
}
