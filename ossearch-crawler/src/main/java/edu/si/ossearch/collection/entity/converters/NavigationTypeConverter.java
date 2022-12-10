package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.collection.entity.DynamicNavigation;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jbirkhimer
 */
public class NavigationTypeConverter extends StdConverter<DynamicNavigation.NavigationType, String> {
    @Override
    public String convert(DynamicNavigation.NavigationType value) {
        return StringUtils.uncapitalize(value.name());
    }
}