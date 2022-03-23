package edu.si.ossearch.dao.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.dao.entity.Collection;
import edu.si.ossearch.security.jwt.models.projections.UserIdNameProjection;
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

    UserIdNameProjection getOwner();

    List<UserIdNameProjection> getUsers();

    //@Projection(name = "CrawlConfigFormData", types = { CrawlConfig.class })
    interface CrawlConfigFormData {
        long getId();

        String getCrawlCronSchedule();
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getDateCreated();
}
