package mugres.ipc.stream.writers;

import mugres.tracker.Track;
import mugres.ipc.protocol.messages.TrackListMessage;

import java.io.DataOutputStream;
import java.io.IOException;

public class TrackListStreamMessageWriter implements StreamMessageWriter<TrackListMessage> {
    @Override
    public void write(final TrackListMessage message, final DataOutputStream dataOutputStream) throws IOException {
        writeMessageType(message.type(), dataOutputStream);
        dataOutputStream.writeInt(message.trackList().size());
        for(final Track track : message.trackList()) {
            dataOutputStream.writeUTF(track.name());
            dataOutputStream.writeInt(track.instrument().id());
        }
    }
}
