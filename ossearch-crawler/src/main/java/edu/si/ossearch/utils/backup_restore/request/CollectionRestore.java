package edu.si.ossearch.utils.backup_restore.request;

import edu.si.ossearch.collection.entity.Collection;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author jbirkhimer
 */
@Slf4j
@Setter
@Getter
@EqualsAndHashCode
@Schema(description = "Restore a Collection, CrawlSchedulerJobInfoInfo, and Users")
public class CollectionRestore implements Serializable {

    private static final long serialVersionUID = -2155684713201509784L;

    private Collection collection;
    private CrawlSchedulerJobInfo crawlSchedulerJobInfo;


}
