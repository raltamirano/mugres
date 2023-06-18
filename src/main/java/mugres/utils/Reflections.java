package mugres.utils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Reflections {
    private Reflections() {}

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPES = new ConcurrentHashMap<>();

    static {
        PRIMITIVE_TYPES.put(Void.class, Void.TYPE);
        PRIMITIVE_TYPES.put(Boolean.class, Boolean.TYPE);
        PRIMITIVE_TYPES.put(Byte.class, Byte.TYPE);
        PRIMITIVE_TYPES.put(Character.class, Character.TYPE);
        PRIMITIVE_TYPES.put(Short.class, Short.TYPE);
        PRIMITIVE_TYPES.put(Integer.class, Integer.TYPE);
        PRIMITIVE_TYPES.put(Long.class, Long.TYPE);
        PRIMITIVE_TYPES.put(Float.class, Float.TYPE);
        PRIMITIVE_TYPES.put(Double.class, Double.TYPE);
    }

    public static Method getMethodFor(final Class<?> clazz, final String propertyName) {
        try { return clazz.getMethod(propertyName); } catch (NoSuchMethodException ignore) {}

        try { return clazz.getMethod("get" + capitalizeFirstLetter(propertyName)); } catch (NoSuchMethodException ignore) {}

        try { return clazz.getMethod("is" + capitalizeFirstLetter(propertyName)); } catch (NoSuchMethodException ignore) {}

        throw new IllegalArgumentException("Couldn't find getter for: " + clazz.getSimpleName() + "->" + propertyName);
    }

    public static Method setMethodFor(final Class<?> clazz, final String propertyName, final Class<?> type) {
        try { return clazz.getMethod(propertyName, type); } catch (NoSuchMethodException ignore) {}

        final Class<?> primitiveType = getPrimitiveType(type);

        if (primitiveType != null)
            try { return clazz.getMethod(propertyName, primitiveType); } catch (NoSuchMethodException ignore) {}

        final String setterName = "set" + capitalizeFirstLetter(propertyName);

        try { return clazz.getMethod(setterName, type); } catch (NoSuchMethodException ignore) {}

        if (primitiveType != null)
            try { return clazz.getMethod(setterName, primitiveType); } catch (NoSuchMethodException ignore) {}

        throw new IllegalArgumentException("Couldn't find setter for: " + clazz.getSimpleName() + "->" + propertyName);
    }

    private static Class<?> getPrimitiveType(final Class<?> type) {
        return PRIMITIVE_TYPES.get(type);
    }

    private static String capitalizeFirstLetter(final String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }
}
