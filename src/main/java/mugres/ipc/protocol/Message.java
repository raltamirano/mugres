package mugres.ipc.protocol;

public class Message {
    private final MessageType type;

    public Message(final MessageType type) {
        if (type == null)
            throw new IllegalArgumentException("type");

        this.type = type;
    }

    public MessageType type() {
        return type;
    }

    @Override
    public String toString() {
        return type.name();
    }
}
