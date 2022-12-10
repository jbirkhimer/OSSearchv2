package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.collection.entity.DynamicNavigation;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class SortTypeConverter extends StdConverter<DynamicNavigation.SortType, String> {
    @Override
    public String convert(DynamicNavigation.SortType value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
