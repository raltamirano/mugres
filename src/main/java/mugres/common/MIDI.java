package mugres.common;

public class MIDI {
    private MIDI() {}

    /** Min MIDI Channel */
    public static final int MIN_CHANNEL = 1;
    /** Max MIDI Channel */
    public static final int MAX_CHANNEL = 16;
    /** Default MIDI Channel */
    public static final int DEFAULT_CHANNEL = MIN_CHANNEL;
    /** End Of Track Meta Message value */
    public static final int END_OF_TRACK = 0x2F;
    /** Percussion channel: 10 */
    public static final int PERCUSSION = 10;

    public static boolean isValidChannel(final int value) {
        return value >= 1 && value <= 16;
    }
    public static boolean isValidNote(final int value) {
        return value >= 0 && value <= 127;
    }
    public static boolean isValidVelocity(final int value) {
        return value >= 0 && value <= 127;
    }
    public static boolean isValidController(final int value) {
        return value >= 0 && value <= 127;
    }
    public static boolean isValidCCValue(final int value) {
        return value >= 0 && value <= 127;
    }
}
