package mugres.core.common;

public class Event {
    private final Length position;
    private final Pitch pitch;
    private final Value value;
    private int velocity;

    public Event(Length position, Pitch pitch, Value value, int velocity) {
        this.position = position;
        this.pitch = pitch;
        this.value = value;
        this.velocity = velocity;
    }

    public Length getPosition() {
        return position;
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
}
