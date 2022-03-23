package edu.si.ossearch.dao.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.dao.entity.Keymatch;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class KeymatchTypeConverter extends StdConverter<Keymatch.KeymatchType, String> {
    @Override
    public String convert(Keymatch.KeymatchType value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
