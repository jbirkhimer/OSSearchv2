package edu.si.ossearch.scheduler.entity.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.scheduler.entity.CrawlLog;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class StateConverter extends StdConverter<CrawlLog.State, String> {
    @Override
    public String convert(CrawlLog.State value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
