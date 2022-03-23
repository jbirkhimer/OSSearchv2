package edu.si.ossearch.scheduler.entity.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.scheduler.entity.CrawlLog;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class StepTypeConverter extends StdConverter<CrawlLog.StepType, String> {
    @Override
    public String convert(CrawlLog.StepType value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
