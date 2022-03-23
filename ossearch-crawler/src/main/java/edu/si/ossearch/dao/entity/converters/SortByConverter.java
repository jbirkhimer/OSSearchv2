package edu.si.ossearch.dao.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.dao.entity.DynamicNavigation;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class SortByConverter extends StdConverter<DynamicNavigation.SortBy, String> {
    @Override
    public String convert(DynamicNavigation.SortBy value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}