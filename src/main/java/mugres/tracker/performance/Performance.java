package mugres.tracker.performance;

import mugres.common.Key;
import mugres.common.Length;
import mugres.common.TimeSignature;
import mugres.common.TrackReference;
import mugres.tracker.Track;

import java.util.*;

public class Performance {
    private final String song;
    private final List<Control.ControlEvent> controlEvents = new ArrayList<>();
    private final Set<mugres.tracker.performance.Track> tracks = new HashSet<>();
    private Length length = Length.ZERO;

    public Performance(String song) {
        this.song = song;
    }

    public String song() {
        return song;
    }

    public List<Control.ControlEvent> controlEvents() {
        return Collections.unmodifiableList(controlEvents);
    }

    public Set<mugres.tracker.performance.Track> tracks() {
        return Collections.unmodifiableSet(tracks);
    }

    public void addControlEvent(final Length position, final int tempo,
                                final Key key, final TimeSignature timeSignature) {
        addControlEvent(position, Control.of(tempo, key, timeSignature));
    }

    public void addControlEvent(final Length position, final Control control) {
        controlEvents.add(Control.ControlEvent.of(position, control));
    }

    public mugres.tracker.performance.Track createTrack(final Track track) {
        if (tracks.stream().anyMatch(t -> t.track().name().equalsIgnoreCase(track.name())))
            throw new IllegalArgumentException("track");

        final mugres.tracker.performance.Track performanceTrack = new mugres.tracker.performance.Track(track);
        tracks.add(performanceTrack);
        return performanceTrack;
    }

    public mugres.tracker.performance.Track track(final TrackReference trackReference) {
        if (trackReference == null)
            throw new IllegalArgumentException("trackReference");

        return tracks.stream().filter(t -> t.track().id().equals(trackReference.trackId()))
                .findFirst().orElse(null);
    }

    public mugres.tracker.performance.Track track(final String trackName) {
        if (trackName == null)
            throw new IllegalArgumentException("trackName");

        return tracks.stream().filter(t -> t.track().name().equals(trackName))
                .findFirst().orElse(null);
    }

    public Length length() {
        return length;
    }

    public void length(final Length length) {
        this.length = length;
    }

    @Override
    public String toString() {
        final StringBuilder sb  = new StringBuilder();

        sb.append(String.format("Song: %s%n", song));

        tracks.forEach(track -> {
            sb.append(String.format("\tTrack: %s%n", track));
            track.events().forEach(event -> {
                sb.append(String.format("\t\t%s%n", event));
            });
        });

        return sb.toString();
    }
}
