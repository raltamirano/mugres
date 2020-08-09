package mugres.core.common;

/** A notated musical event, with a known duration. */
public class Event {
    private Length position;
    private final Value value;
    private final Played played;

    private Event(final Length position, final Pitch pitch, final Value value, final int velocity) {
        this.position = position;
        this.value = value;
        this.played = Played.of(pitch, velocity);
    }

    public static Event of(final Length position, final Pitch pitch, final Value value, final int velocity) {
        return new Event(position, pitch, value, velocity);
    }

    public Length getPosition() {
        return position;
    }

    public void offset(final Length by) {
        position = position.plus(by);
    }

    public Value getValue() {
        return value;
    }

    public Played getPlayed() { return played; }

    @Override
    public String toString() {
        return String.format("%s %-13s @ %6s ", played, value, position);
    }
}
