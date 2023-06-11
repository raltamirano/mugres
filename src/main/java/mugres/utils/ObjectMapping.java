package mugres.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ObjectMapping {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ObjectMapping() {}

    public static <X> X mapToPojo(final Map<String, Object> map, final Class<X> clazz) {
        return (map == null) ? null : MAPPER.convertValue(map, clazz);
    }

    public static Map<String, Object> pojoToMap(final Object pojo) {
        return (pojo == null) ? null : MAPPER.convertValue(pojo, new TypeReference<Map<String, Object>>() {});
    }
}
