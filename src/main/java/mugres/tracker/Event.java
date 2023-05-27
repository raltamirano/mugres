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

    private Event(final Length position, final Pitch pitch, final Length length, final Value value, final int velocity) {
        if (position == null)
            throw new IllegalArgumentException("position");
        if (pitch == null)
            throw new IllegalArgumentException("pitch");

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

    public Length position() {
        return position;
    }

    public Length length() {
        return length;
    }

    public Event offset(final Length by) {
        return new Event(position.plus(by), pitch, length, value, velocity);
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

    @Override
    public String toString() {
        return String.format("%s (%03d) %-13s @ %6s ", pitch, velocity, value, position);
    }
}
