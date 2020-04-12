package mugres.core.performance;

import mugres.core.common.Key;
import mugres.core.common.Length;
import mugres.core.common.TimeSignature;
import mugres.core.common.Party;

import java.util.*;

public class Performance {
    private final String song;
    private final List<Control.ControlEvent> controlEvents = new ArrayList<>();
    private final Set<Track> tracks = new HashSet<>();

    public Performance(String song) {
        this.song = song;
    }

    public String getSong() {
        return song;
    }

    public List<Control.ControlEvent> getControlEvents() {
        return Collections.unmodifiableList(controlEvents);
    }

    public Set<Track> getTracks() {
        return Collections.unmodifiableSet(tracks);
    }

    public void addControlEvent(final Length position, final int tempo,
                                final Key key, final TimeSignature timeSignature) {
        addControlEvent(position, Control.of(tempo, key, timeSignature));
    }

    public void addControlEvent(final Length position, final Control control) {
        controlEvents.add(Control.ControlEvent.of(position, control));
    }

    public Track createTrack(final Party party) {
        if (tracks.stream().anyMatch(t -> t.getParty().equalsIgnoreCase(party.getName())))
            throw new IllegalArgumentException("party");

        final Track track = new Track(party.getName(), party.getChannel());
        tracks.add(track);
        return track;
    }

    @Override
    public String toString() {
        final StringBuilder sb  = new StringBuilder();

        sb.append(String.format("Song: %s%n", song));

        tracks.forEach(track -> {
            sb.append(String.format("\tTrack: %s%n", track));
            track.getEvents().forEach(event -> {
                sb.append(String.format("\t\t%s%n", event));
            });
        });

        return sb.toString();
    }
}
