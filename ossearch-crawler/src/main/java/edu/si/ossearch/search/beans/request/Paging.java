package edu.si.ossearch.search.beans.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jbirkhimer
 */
@Slf4j
@Setter
@Getter
@EqualsAndHashCode
@Schema(description = "Retrieve by Paging")
public class Paging {
    private String action = "";
    private String submit = "";
    private String oldquery = "";
    private Integer rowold;
    private Integer currentPage;
    private String btnG;
}
