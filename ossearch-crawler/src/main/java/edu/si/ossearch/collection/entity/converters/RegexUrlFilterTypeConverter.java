package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.collection.entity.RegexUrlFilters;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class RegexUrlFilterTypeConverter extends StdConverter<RegexUrlFilters.Type, String> {
    @Override
    public String convert(RegexUrlFilters.Type value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}