package mugres.core;

import mugres.core.function.Call;
import mugres.core.notation.Section;
import mugres.core.notation.Song;
import mugres.core.performance.Performance;
import mugres.core.performance.Performer;
import mugres.core.performance.Track;
import mugres.core.performance.converters.ToMIDISequenceConverter;
import org.junit.jupiter.api.Test;

import javax.sound.midi.Sequence;

import static mugres.core.common.Context.createBasicContext;
import static mugres.core.function.Function.RANDOM;
import static mugres.core.notation.Party.GUITAR1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConvertToMIDITests {
    @Test
    public void convertSimpleSong() {
        final Song song = Song.of("We will unit test you", createBasicContext());
        final Section section = song.createSection("A", 2);
        song.getArrangement().addEntry(section, 1);

        section.addPart(GUITAR1, Call.of(RANDOM, section.getMeasures()));

        final Performance performance = Performer.perform(song);
        System.out.println(String.format("Performance =>%n%s", performance));

        assertNotNull(performance);
        assertEquals(song.getTitle(), performance.getSong());
        assertEquals(1, performance.getTracks().size());
        final Track track = performance.getTracks().iterator().next();
        assertEquals(GUITAR1.getName(), track.getParty());
        // 2 measures of random quarter notes (section A repeats once)
        assertEquals(8, track.getEvents().size());

        // Conversion
        final ToMIDISequenceConverter converter = new ToMIDISequenceConverter();
        final Sequence sequence = converter.convert(performance);

        assertNotNull(sequence);
        // # tracks for a type 1 MIDI file = one for control of tempo, key, etc + one per party
        assertEquals(song.getParties().size() + 1, sequence.getTracks().length);
        // Expected events: set track name, tempo, time signature, and end-of-track
        assertEquals(4, sequence.getTracks()[0].size());
        // Expected events: one set track name, 16 note events (8 notes, each one get both a NOTE_ON
        // and a NOTE_OFF event) and end-of-track
        assertEquals(18, sequence.getTracks()[1].size());
    }
}

