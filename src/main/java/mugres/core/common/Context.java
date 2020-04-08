package mugres.core.common;

/** Context data for all things musical. */
public interface Context {
    default int getTempo() { return 120; }
    default Key getKey() { return  Key.C; }
    default TimeSignature getTimeSignature() { return TimeSignature.TS44; }
}
