package edu.si.ossearch.dao.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.dao.entity.RegexUrlFilters;

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