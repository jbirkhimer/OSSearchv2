package edu.si.ossearch.dao.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.google.common.base.CaseFormat;
import edu.si.ossearch.dao.entity.DynamicNavigation;
import org.apache.commons.lang.WordUtils;
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