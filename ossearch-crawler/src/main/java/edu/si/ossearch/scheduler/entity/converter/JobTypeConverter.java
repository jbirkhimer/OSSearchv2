package edu.si.ossearch.scheduler.entity.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.scheduler.entity.CrawlSchedulerJobInfo;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class JobTypeConverter extends StdConverter<CrawlSchedulerJobInfo.JobType, String> {
    @Override
    public String convert(CrawlSchedulerJobInfo.JobType value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
