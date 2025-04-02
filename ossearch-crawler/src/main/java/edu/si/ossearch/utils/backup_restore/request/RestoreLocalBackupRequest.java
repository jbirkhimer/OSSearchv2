package edu.si.ossearch.utils.backup_restore.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Slf4j
@Setter
@Getter
@EqualsAndHashCode
@Schema(description = "Restore a Collection, CrawlSchedulerJobInfoInfo, and Users")
public class RestoreLocalBackupRequest implements Serializable {
    private static final long serialVersionUID = 5397767048747673723L;

    @NotNull
    List<String> files;
    Boolean restoreCollection = true;
    Boolean restoreCrawlSchedule = true;
    Boolean restoreUsers = true;
}
