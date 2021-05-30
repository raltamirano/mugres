package mugres.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private Utils() {}

    public static List<Integer> rangeClosed(final int start, final int end) {
        final List<Integer> items = new ArrayList<>();
        for (int index = start; index <= end; index++)
            items.add(index);
        return items;
    }

    public static Map<String, Object> toMap(final String key1, final Object value1) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        return map;
    }

    public static Map<String, Object> toMap(final String key1, final Object value1,
                          final String key2, final Object value2) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    public static Map<String, Object> toMap(final String key1, final Object value1,
                          final String key2, final Object value2,
                          final String key3, final Object value3) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    public static Map<String, Object> toMap(final String key1, final Object value1,
                          final String key2, final Object value2,
                          final String key3, final Object value3,
                          final String key4, final Object value4) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }
}
