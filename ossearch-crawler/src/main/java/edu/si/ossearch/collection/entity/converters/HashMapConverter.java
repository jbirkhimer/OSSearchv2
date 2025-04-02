package edu.si.ossearch.collection.entity.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jbirkhimer
 */
@Slf4j
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> objectMap) {

        String json = null;
        try {
            json = objectMapper.writeValueAsString(objectMap);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return json;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String json) {

        Map<String, Object> objectMap = null;
        try {
            objectMap = objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return objectMap;
    }

}
