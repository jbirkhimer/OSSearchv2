package edu.si.ossearch.utils.backup_restore.entity.projections;

import edu.si.ossearch.collection.entity.projections.CollectionExport;

/**
 * @author jbirkhimer
 */
public interface CollectionRestoreProjection {
    CollectionExport getCollection();
    CrawlSchedulerJobInfoInfoExport getCrawlSchedulerJobInfoInfo();
}
