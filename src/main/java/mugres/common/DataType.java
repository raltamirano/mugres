package mugres.common;

public enum DataType {
    /** {@link Length} */
    LENGTH,
    /** {@link Value} */
    VALUE,
    /** {@link Note} */
    NOTE,
    /** {@link Pitch} */
    PITCH,
    /** {@link Scale} */
    SCALE,
    /** Plain text */
    TEXT,
    /** Integer numbers */
    INTEGER,
    /** A DrumKit piece*/
    DRUM_KIT,
    /** Variants of something */
    VARIANT,
    /** True/False values */
    BOOLEAN,
    /** Euclidean pattern */
    EUCLIDEAN_PATTERN,
    /** Unknown */
    UNKNOWN
}
