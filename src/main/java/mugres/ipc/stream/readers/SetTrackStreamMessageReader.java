package mugres.ipc.stream.readers;

import mugres.common.Instrument;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.protocol.messages.SetTrackMessage;

import java.io.DataInputStream;
import java.io.IOException;

public class SetTrackStreamMessageReader implements StreamMessageReader<SetTrackMessage> {
    @Override
    public SetTrackMessage read(final MessageType messageType, final DataInputStream dataInputStream) throws IOException {
        return SetTrackMessage.of(Instrument.of(dataInputStream.readInt()));
    }
}
