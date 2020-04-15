package mugres.core.common;

public class Event {
    private Length position;
    private Played played;

    private Event(final Length position, final Pitch pitch, final Value value, final int velocity) {
        this.position = position;
        this.played = Played.of(pitch, value, velocity);
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

    public Played getPlayed() { return played; }

    @Override
    public String toString() {
        return String.format("%s @ %6s ", played, position);
    }
}
