package edu.si.ossearch.edan;

import lombok.Builder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Builder
public class EdanRequest {

    String q = "*:*";

    String fqs;

    Boolean facet = false;

    List<String> facetField = new ArrayList<>();

    Boolean omitContent;

    String linkedContent;

    String profile = "search";

    Boolean includePrivate = false;

    @Min(value = 0)
    @Max(value = 100)
    Integer rows = 10;

    String sortDir = "asc";

    @Min(value = 0)
    @Max(value = 20000)
    Integer start = 0;

    public String toQueryString() {

        return "";
    }
}
