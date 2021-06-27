package mugres.core;

import mugres.core.function.Call;
import mugres.core.function.Function;
import mugres.core.function.builtin.random.Random;
import mugres.core.notation.Section;
import mugres.core.notation.Song;
import mugres.core.notation.performance.Performance;
import mugres.core.notation.performance.Performer;
import mugres.core.notation.performance.Track;
import mugres.core.notation.performance.converters.ToMidiSequenceConverter;
import org.junit.jupiter.api.Test;

import javax.sound.midi.Sequence;

import static mugres.core.common.Context.basicContext;
import static mugres.core.common.Party.WellKnownParties.GUITAR1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConvertToMidiTests {
    @Test
    public void convertSimpleSong() {
        final Song song = Song.of("We will unit test you", basicContext());
        final Section section = song.createSection("A", 2);
        song.arrangement().append(section, 1);

        section.addPart(GUITAR1.party(), Call.of(random(), section.measures()));

        final Performance performance = Performer.perform(song);
        System.out.println(String.format("Performance =>%n%s", performance));

        assertNotNull(performance);
        assertEquals(song.title(), performance.song());
        assertEquals(1, performance.tracks().size());
        final Track track = performance.tracks().iterator().next();
        assertEquals(GUITAR1.party().name(), track.party());
        // 2 measures of random quarter notes (section A repeats once)
        assertEquals(8, track.events().size());

        // Conversion
        final Sequence sequence = ToMidiSequenceConverter.getInstance().convert(performance);

        assertNotNull(sequence);
        // # tracks for a type 1 Midi file = one for control of tempo, key, etc + one per party
        assertEquals(song.parties().size() + 1, sequence.getTracks().length);
        // Expected events: set track name, tempo, time signature, and end-of-track
        assertEquals(4, sequence.getTracks()[0].size());
        // Expected events: one set track name, 16 note events (8 notes, each one get both a NOTE_ON
        // and a NOTE_OFF event) and end-of-track
        assertEquals(18, sequence.getTracks()[1].size());
    }

    public Random random() {
        return Function.forName("random");
    }
}

