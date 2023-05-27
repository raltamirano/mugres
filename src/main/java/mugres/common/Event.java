package mugres.common;

/** A musical event */
public class Event {
    private Length position;
    private final Value value;
    private final Length length;
    private final Played played;

    private Event(final Length position, final Pitch pitch, final Length length, final Value value, final int velocity) {
        if (position == null)
            throw new IllegalArgumentException("position");
        if (pitch == null)
            throw new IllegalArgumentException("pitch");
//        if (length != null && value != null)
//            throw new IllegalArgumentException("Do not provide both length and value for events!");

        this.position = position;
        this.length = length;
        this.value = value;
        this.played = Played.of(pitch, velocity);
    }

    public static Event of(final Length position, final Pitch pitch, final Length length, final int velocity) {
        return new Event(position, pitch, length, null, velocity);
    }

    public static Event of(final Length position, final Pitch pitch, final Value value, final int velocity) {
        return new Event(position, pitch, value.length(), value, velocity);
    }

    public Length position() {
        return position;
    }

    public Length length() {
        return length;
    }

    public Event offset(final Length by) {
        return new Event(position.plus(by), played.pitch(), length, value, played.velocity());
    }

    public Played played() { return played; }

    @Override
    public String toString() {
        return String.format("%s %-13s @ %6s ", played, value, position);
    }
}
