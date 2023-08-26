package mugres.common;

import mugres.common.chords.ChordMode;
import mugres.common.euclides.EuclideanPattern;
import mugres.common.literal.Literal;
import mugres.common.ttm.TwelveToneMatrix;
import mugres.function.builtin.follower.FollowerChord;
import mugres.tracker.Track;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static mugres.utils.Reflections.getMethodFor;
import static mugres.utils.Reflections.setMethodFor;
import static mugres.utils.Utils.defaultValue;

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
    /** Literal */
    LITERAL(Literal.class),
    /** Twelve Tone Matrix */
    TWELVE_TONE_MATRIX(TwelveToneMatrix.class),
    /** Instrument */
    INSTRUMENT(Instrument.class),
    /** Object */
    OBJECT(Object.class),
    /** Chord Mode */
    CHORD_MODE(ChordMode.class),
    /** Interval */
    INTERVAL(Interval.class),
    /** Interval Type */
    INTERVAL_TYPE(IntervalType.class),
    /** Octave */
    OCTAVE(Octave.class),
    /** Scale Correction */
    SCALE_CORRECTION(ScaleCorrection.class),
    /** Track */
    TRACK(TrackReference.class),
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

    public void clear(final Object target, final String propertyName) {
        try {
            final Method setter = setMethodFor(target.getClass(), propertyName, baseType);
            setter.invoke(target, defaultValue(setter.getParameterTypes()[0]));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
