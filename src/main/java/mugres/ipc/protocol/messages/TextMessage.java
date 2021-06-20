package mugres.ipc.protocol.messages;

import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

public class TextMessage extends Message {
    private final String text;

    private TextMessage(final String text) {
        super(MessageType.TEXT);

        if (text == null)
            throw new IllegalArgumentException("text");

        this.text = text;
    }

    public static TextMessage of(final String text) {
        return new TextMessage(text);
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + text;
    }
}
