package mugres.tracker.performance;

import mugres.tracker.Event;
import mugres.common.Instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Track {
    private final mugres.tracker.Track track;
    private final List<Event> events = new ArrayList<>();

    public Track(final mugres.tracker.Track track) {
        this.track = track;
    }

    public mugres.tracker.Track track() {
        return track;
    }

    public int channel() {
        return track.channel();
    }

    public Instrument instrument() {
        return track.instrument();
    }

    public List<Event> events() {
        return Collections.unmodifiableList(events);
    }

    public Track addEvent(final Event event) {
        events.add(event);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return this.track.equals(track.track);
    }

    @Override
    public int hashCode() {
        return Objects.hash(track);
    }

    @Override
    public String toString() {
        return track.toString();
    }
}
