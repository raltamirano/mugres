package mugres.ipc.stream.readers;

import mugres.core.common.Instrument;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.protocol.messages.SetPartyMessage;

import java.io.DataInputStream;
import java.io.IOException;

public class SetPartyStreamMessageReader implements StreamMessageReader<SetPartyMessage> {
    @Override
    public SetPartyMessage read(final MessageType messageType, final DataInputStream dataInputStream) throws IOException {
        return SetPartyMessage.of(Instrument.getById(dataInputStream.readInt()));
    }
}
