package mugres.common;

import java.util.UUID;

public class TrackReference {
    private final UUID trackId;

    private TrackReference(final UUID trackId) {
        if (trackId == null)
            throw new IllegalArgumentException("trackId");

        this.trackId = trackId;
    }

    public static TrackReference of(final UUID trackId) {
        return new TrackReference(trackId);
    }

    public UUID trackId() {
        return trackId;
    }
}
