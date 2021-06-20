package mugres.ipc.protocol;

public enum MessageType {
    SIGNALS(0),
    BYE(1),
    TEXT(2);

    private final int identifier;

    MessageType(final int identifier) {
        this.identifier = identifier;
    }

    public static MessageType forIdentifier(final int identifier) {
        for(MessageType m : values())
            if (m.identifier == identifier)
                return m;

        throw new IllegalArgumentException("Unknown Message Type identifier: " + identifier);
    }

    public int identifier() {
        return identifier;
    }
}
