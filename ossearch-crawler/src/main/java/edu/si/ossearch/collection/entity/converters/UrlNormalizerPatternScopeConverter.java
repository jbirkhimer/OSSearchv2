package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.collection.entity.URLNormalizerPattern;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class UrlNormalizerPatternScopeConverter extends StdConverter<URLNormalizerPattern.Scope, String> {
    @Override
    public String convert(URLNormalizerPattern.Scope value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
