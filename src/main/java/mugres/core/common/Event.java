package mugres.core.common;

public class Event {
    private Length position;
    private final Pitch pitch;
    private final Value value;
    private int velocity;

    private Event(final Length position, final Pitch pitch, final Value value, final int velocity) {
        this.position = position;
        this.pitch = pitch;
        this.value = value;
        this.velocity = velocity;
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

    @Override
    public String toString() {
        return String.format("%s %-13s @ %6s (%03d)", pitch, value, position, velocity);
    }
}
