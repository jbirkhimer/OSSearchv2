package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.collection.entity.UrlExclusionPattern;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class UrlExclusionPatternScopeConverter extends StdConverter<UrlExclusionPattern.Scope, String> {
    @Override
    public String convert(UrlExclusionPattern.Scope value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
