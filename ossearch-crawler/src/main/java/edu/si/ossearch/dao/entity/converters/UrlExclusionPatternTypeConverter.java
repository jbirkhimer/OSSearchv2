package edu.si.ossearch.dao.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.dao.entity.UrlExclusionPattern;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class UrlExclusionPatternTypeConverter extends StdConverter<UrlExclusionPattern.Type, String> {
    @Override
    public String convert(UrlExclusionPattern.Type value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}