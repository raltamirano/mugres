package mugres.ipc.stream.readers;

import mugres.common.Instrument;
import mugres.tracker.Track;
import mugres.ipc.protocol.MessageType;
import mugres.ipc.protocol.messages.TrackListMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrackListStreamMessageReader implements StreamMessageReader<TrackListMessage> {
    @Override
    public TrackListMessage read(final MessageType messageType, final DataInputStream dataInputStream) throws IOException {
        final int numberOfTracks = dataInputStream.readInt();

        final List<Track> trackList = new ArrayList<>();
        for(int i=0; i<numberOfTracks; i++) {
            final String name = dataInputStream.readUTF();
            final Instrument instrument = Instrument.of(dataInputStream.readInt());
            trackList.add(Track.of(name, instrument));
        }
        return TrackListMessage.of(trackList);
    }
}
