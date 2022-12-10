package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.collection.entity.UrlExclusionPattern;

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