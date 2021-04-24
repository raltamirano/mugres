package mugres.core.common;

/** A specific note played. */
public class Played implements Cloneable {
    private final Pitch pitch;
    private int velocity;

    private Played(final Pitch pitch, final int velocity) {
        if (velocity < 0 || velocity > 127)
            throw new IllegalArgumentException("velocity");

        this.pitch = pitch;
        this.velocity = velocity;
    }

    public static Played of(final int packed) {
        return of(Pitch.of(packed/1000), packed % 1000);
    }

    public static Played of(final Pitch pitch, final int velocity) {
        return new Played(pitch, velocity);
    }

    public Pitch getPitch() {
        return pitch;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public Played pitchUp(final int semitones) {
        return of(pitch.up(semitones), velocity);
    }

    public Played pitchUp(final Interval interval) {
        return of(pitch.up(interval), velocity);
    }

    public Played pitchDown(final int semitones) {
        return of(pitch.down(semitones), velocity);
    }

    public Played pitchDown(final Interval interval) {
        return of(pitch.down(interval), velocity);
    }

    public Played repitch(Pitch newPitch) {
        return of(newPitch, velocity);
    }

    public int pack() {
        return pitch.getMidi() * 1000 + velocity;
    }

    @Override
    protected Played clone() {
        return of(pitch, velocity);
    }

    public String toString() {
        return String.format("%s (%03d)", pitch, velocity);
    }
}
