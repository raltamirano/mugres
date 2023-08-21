package mugres.tracker;

import mugres.common.Length;
import mugres.common.Pitch;
import mugres.common.Value;

/** A musical event */
public class Event {
    private Length position;
    private final Value value;
    private final Length length;
    private final Pitch pitch;
    private int velocity;

    private Event(final Length position, final Length length, final Value value) {
        if (position == null)
            throw new IllegalArgumentException("position");
        if (length == null || length.isEmpty())
            throw new IllegalArgumentException("length");

        this.position = position;
        this.length = length;
        this.pitch = null;
        this.velocity = -1;
        this.value = value;
    }

    private Event(final Length position, final Pitch pitch, final Length length, final Value value, final int velocity) {
        if (position == null)
            throw new IllegalArgumentException("position");
        if (pitch == null)
            throw new IllegalArgumentException("pitch");
        if (length == null || length.isEmpty())
            throw new IllegalArgumentException("length");
        if (velocity < 0)
            throw new IllegalArgumentException("velocity");

        this.position = position;
        this.length = length;
        this.value = value;
        this.pitch = pitch;
        this.velocity = velocity;
    }

    public static Event of(final Length position, final Pitch pitch, final Length length, final int velocity) {
        return new Event(position, pitch, length, null, velocity);
    }

    public static Event of(final Length position, final Pitch pitch, final Value value, final int velocity) {
        return new Event(position, pitch, value.length(), value, velocity);
    }

    public static Event rest(final Length position, final Value value) {
        return new Event(position, value.length(), value);
    }

    public static Event rest(final Length position, final Length length) {
        return new Event(position, length, null);
    }

    public Length position() {
        return position;
    }

    public Length length() {
        return length;
    }

    public Value value() {
        return value;
    }

    public Event offset(final Length by) {
        final Length newPosition = position.plus(by);
        return rest() ?
                new Event(newPosition, length, value) :
                new Event(newPosition, pitch, length, value, velocity);
    }

    public Pitch pitch() {
        return pitch;
    }

    public int velocity() {
        return velocity;
    }

    public void velocity(final int velocity) {
        this.velocity = velocity;
    }

    public boolean rest() {
        return pitch == null && velocity == -1;
    }

    @Override
    public String toString() {
        return String.format("%s (%03d) %-13s @ %6s ", pitch, velocity, value, position);
    }
}
