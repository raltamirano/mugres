package mugres.core.notation.performance;

import mugres.core.common.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Track {
    private final String party;
    private final int channel;
    private final List<Event> events = new ArrayList<>();

    public Track(final String party, final int channel) {
        this.party = party;
        this.channel = channel;
    }

    public String getParty() {
        return party;
    }

    public int getChannel() {
        return channel;
    }

    public List<Event> getEvents() {
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
        return party.equals(track.party);
    }

    @Override
    public int hashCode() {
        return Objects.hash(party);
    }

    @Override
    public String toString() {
        return party;
    }
}
