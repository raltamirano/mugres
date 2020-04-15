package mugres.core.common;

/** A specific note played. Basically a NOTE ON + NOTE OFF combined into a single entity. */
public class Played {
    private final Pitch pitch;
    private final Value value;
    private int velocity;

    private Played(final Pitch pitch, final Value value, final int velocity) {
        this.pitch = pitch;
        this.value = value;
        this.velocity = velocity;
    }

    public static Played of(final Pitch pitch, final Value value, final int velocity) {
        return new Played(pitch, value, velocity);
    }

    public Pitch getPitch() {
        return pitch;
    }

    public Value getValue() {
        return value;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public String toString() {
        return String.format("%s %-13s (%03d)", pitch, value, velocity);
    }
}
