package edu.si.ossearch.nutch.entity.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.si.ossearch.nutch.entity.Webpage;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

/**
 * @author jbirkhimer
 */
@Projection(name = "webpageInfoTableData", types = {Webpage.class})
public interface WebpageInfoTableData {
    String getUrl();

    String getStatusName();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getFetchTime();
}
