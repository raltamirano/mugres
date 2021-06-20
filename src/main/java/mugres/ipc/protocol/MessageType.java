package mugres.ipc.protocol;

public enum MessageType {
    SIGNALS(0, true, false),
    BYE(1, true, false),
    TEXT(2, false, false),
    SET_PARTY(3, true, false),
    PARTY_LIST(4, false, true);

    private final int identifier;
    private final boolean toServerOnly;
    private final boolean fromServerOnly;

    MessageType(final int identifier, final boolean toServerOnly, final boolean fromServerOnly) {
        this.identifier = identifier;
        this.toServerOnly = toServerOnly;
        this.fromServerOnly = fromServerOnly;
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

    public boolean toServerOnly() {
        return toServerOnly;
    }

    public boolean fromServerOnly() {
        return fromServerOnly;
    }
}
