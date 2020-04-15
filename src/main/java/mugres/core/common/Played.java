package mugres.core.common;

/** A specific note played. */
public class Played {
    private final Pitch pitch;
    private int velocity;

    private Played(final Pitch pitch,final int velocity) {
        this.pitch = pitch;
        this.velocity = velocity;
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

    public String toString() {
        return String.format("%s (%03d)", pitch, velocity);
    }
}
