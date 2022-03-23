package edu.si.ossearch.dao.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.dao.entity.Collection;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class ResponseTypeConverter extends StdConverter<Collection.ResponseType, String> {
    @Override
    public String convert(Collection.ResponseType value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
