package mugres.ipc.protocol.messages;

import mugres.common.Track;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.MessageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackListMessage extends Message {
    private final List<Track> trackList;

    private TrackListMessage(final List<Track> trackList) {
        super(MessageType.TRACK_LIST);

        if (trackList == null)
            throw new IllegalArgumentException("trackList");

        this.trackList = new ArrayList<>(trackList);
    }

    public static TrackListMessage of(final List<Track> trackList) {
        return new TrackListMessage(trackList);
    }

    public List<Track> trackList() {
        return Collections.unmodifiableList(trackList);
    }

    @Override
    public String toString() {
        return super.toString() + " - Track list: " + trackList;
    }
}
