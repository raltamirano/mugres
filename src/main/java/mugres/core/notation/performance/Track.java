package mugres.core.notation.performance;

import mugres.core.common.Event;
import mugres.core.common.Instrument;
import mugres.core.common.Party;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Track {
    private final Party party;
    private final List<Event> events = new ArrayList<>();

    public Track(final Party party) {
        this.party = party;
    }

    public Party party() {
        return party;
    }

    public int channel() {
        return party.channel();
    }

    public Instrument instrument() {
        return party.instrument();
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
        return party.equals(track.party);
    }

    @Override
    public int hashCode() {
        return Objects.hash(party);
    }

    @Override
    public String toString() {
        return party.toString();
    }
}
