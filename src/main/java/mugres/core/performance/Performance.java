package mugres.core.performance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Performance {
    private final String song;
    private final Set<Track> tracks = new HashSet<>();

    public Performance(String song) {
        this.song = song;
    }

    public String getSong() {
        return song;
    }

    public Set<Track> getTracks() {
        return Collections.unmodifiableSet(tracks);
    }

    public Track createTrack(final String party) {
        if (tracks.stream().anyMatch(t -> t.getParty().equalsIgnoreCase(party)))
            throw new IllegalArgumentException("party");

        final Track track = new Track(party);
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
