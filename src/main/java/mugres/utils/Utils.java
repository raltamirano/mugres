package mugres.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private Utils() {}

    // These gets initialized to their default values
    private static boolean DEFAULT_BOOLEAN;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;
    private static char DEFAULT_CHAR;

    public static Object defaultValue(final Class<?> clazz) {
        if (clazz.equals(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (clazz.equals(byte.class)) {
            return DEFAULT_BYTE;
        } else if (clazz.equals(short.class)) {
            return DEFAULT_SHORT;
        } else if (clazz.equals(int.class)) {
            return DEFAULT_INT;
        } else if (clazz.equals(long.class)) {
            return DEFAULT_LONG;
        } else if (clazz.equals(float.class)) {
            return DEFAULT_FLOAT;
        } else if (clazz.equals(double.class)) {
            return DEFAULT_DOUBLE;
        } else if (clazz.equals(char.class)) {
            return DEFAULT_CHAR;
        } else {
           return null;
        }
    }

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

    public static Map<String, Object> toMap(final String key1, final Object value1,
                          final String key2, final Object value2,
                          final String key3, final Object value3,
                          final String key4, final Object value4,
                          final String key5, final Object value5) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        return map;
    }

    public static Map<String, Object> toMap(final String key1, final Object value1,
                          final String key2, final Object value2,
                          final String key3, final Object value3,
                          final String key4, final Object value4,
                          final String key5, final Object value5,
                          final String key6, final Object value6) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        return map;
    }

    public static Map<String, Object> toMap(final String key1, final Object value1,
                          final String key2, final Object value2,
                          final String key3, final Object value3,
                          final String key4, final Object value4,
                          final String key5, final Object value5,
                          final String key6, final Object value6,
                          final String key7, final Object value7) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        return map;
    }

    public static Map<String, Object> toMap(final String key1, final Object value1,
                          final String key2, final Object value2,
                          final String key3, final Object value3,
                          final String key4, final Object value4,
                          final String key5, final Object value5,
                          final String key6, final Object value6,
                          final String key7, final Object value7,
                          final String key8, final Object value8) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        map.put(key8, value8);
        return map;
    }
}
