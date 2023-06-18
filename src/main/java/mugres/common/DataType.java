package mugres.common;

import mugres.common.euclides.EuclideanPattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static mugres.utils.Reflections.getMethodFor;
import static mugres.utils.Reflections.setMethodFor;

public enum DataType {
    /** {@link Length} */
    LENGTH(Length.class),
    /** {@link Value} */
    VALUE(Value.class),
    /** {@link Note} */
    NOTE(Note.class),
    /** {@link Pitch} */
    PITCH(Pitch.class),
    /** {@link Scale} */
    SCALE(Scale.class),
    /** {@link Key} */
    KEY(Key.class),
    /** {@link TimeSignature} */
    TIME_SIGNATURE(TimeSignature.class),
    /** Plain text */
    TEXT(String.class),
    /** Integer numbers */
    INTEGER(Integer.class),
    /** A DrumKit piece*/
    DRUM_KIT(DrumKit.class),
    /** Variants of something */
    VARIANT(Variant.class),
    /** True/False values */
    BOOLEAN(Boolean.class),
    /** Euclidean pattern */
    EUCLIDEAN_PATTERN(EuclideanPattern.class),
    /** Unknown */
    UNKNOWN(Object.class);

    private final Class<?> baseType;

    DataType(final Class<?> baseType) {
        this.baseType = baseType;
    }

    public Class<?> baseType() {
        return baseType;
    }

    public void set(final Object target, final String propertyName, final Object value) {
        try {
            setMethodFor(target.getClass(), propertyName, baseType).invoke(target, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(final Object target, final String propertyName) {
        try {
            return getMethodFor(target.getClass(), propertyName).invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static String capitalizeFirstLetter(final String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }
}
