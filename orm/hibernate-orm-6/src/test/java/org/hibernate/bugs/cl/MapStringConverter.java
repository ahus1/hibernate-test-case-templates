package org.hibernate.bugs.cl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class MapStringConverter implements AttributeConverter<Map<String, String>, String> {
    private static final Logger logger = Logger.getLogger(MapStringConverter.class);

    public static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (IOException e) {
            logger.error("Error while converting Map to JSON String: ", e);
            return null;
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            logger.error("Error while converting JSON String to Map: ", e);
            return null;
        }
    }
}
