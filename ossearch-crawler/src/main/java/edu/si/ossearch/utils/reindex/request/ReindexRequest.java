package edu.si.ossearch.utils.reindex.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jbirkhimer
 */
@Slf4j
@Setter
@Getter
@EqualsAndHashCode
@Schema(description = "Reindex Collection Request")
public class ReindexRequest implements Serializable {
    private static final long serialVersionUID = -6127657296721042705L;
    public static final String DEFAULT_JOB_GROUP_NAME = "scheduled_crawl";

    @NotNull
    String jobName;
    String groupName = DEFAULT_JOB_GROUP_NAME;
}
