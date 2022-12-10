package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import edu.si.ossearch.collection.entity.Keymatch;

import java.util.Locale;

/**
 * @author jbirkhimer
 */
public class CreationTypeConverter extends StdConverter<Keymatch.CreationType, String> {
    @Override
    public String convert(Keymatch.CreationType value) {
        return value.name().toLowerCase(Locale.ROOT);
    }
}
