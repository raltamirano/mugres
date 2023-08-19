package mugres;

import mugres.function.Call;
import mugres.function.Function;
import mugres.function.builtin.random.Random;
import mugres.tracker.Pattern;
import mugres.tracker.Song;
import mugres.tracker.performance.Performance;
import mugres.tracker.performance.Performer;
import mugres.tracker.performance.Track;
import mugres.tracker.performance.converters.ToMidiSequenceConverter;
import org.junit.jupiter.api.Test;

import javax.sound.midi.Sequence;

import static mugres.common.Context.basicContext;
import static mugres.tracker.Track.WellKnownTracks.GUITAR1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConvertToMidiTests {
    @Test
    public void convertSimpleSong() {
        final Song song = Song.of("We will unit test you", basicContext());
        final Pattern pattern = song.createPattern("A", 2);
        song.arrangement().append(pattern, 1);

        pattern.addPart(GUITAR1.track(), Call.of(random(), pattern.measures()));

        final Performance performance = Performer.perform(song);
        System.out.println(String.format("Performance =>%n%s", performance));

        assertNotNull(performance);
        assertEquals(song.name(), performance.song());
        assertEquals(1, performance.tracks().size());
        final Track track = performance.tracks().iterator().next();
        assertEquals(GUITAR1.track().name(), track.track());
        // 2 measures of random quarter notes (pattern A repeats once)
        assertEquals(8, track.events().size());

        // Conversion
        final Sequence sequence = ToMidiSequenceConverter.getInstance().convert(performance);

        assertNotNull(sequence);
        // # tracks for a type 1 Midi file = one for control of tempo, key, etc + one per song's track
        assertEquals(song.tracks().size() + 1, sequence.getTracks().length);
        // Expected events: set track name, tempo, time signature, and end-of-track
        assertEquals(4, sequence.getTracks()[0].size());
        // Expected events: one set track name, 16 note events (8 notes, each one get both a NOTE_ON
        // and a NOTE_OFF event) and end-of-track
        assertEquals(18, sequence.getTracks()[1].size());
    }

    public Random random() {
        return (Random) Function.forName("random");
    }
}

