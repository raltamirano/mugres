package mugres.core;

import mugres.core.function.Call;
import mugres.core.notation.Section;
import mugres.core.notation.Song;
import mugres.core.performance.Performance;
import mugres.core.performance.Track;
import org.junit.jupiter.api.Test;

import static mugres.core.common.Context.createBasicContext;
import static mugres.core.function.Function.RANDOM;
import static mugres.core.notation.Party.GUITAR1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExportToMIDITests {
    @Test
    public void exportSimpleSong() {
        final Song song = Song.of("We will unit test you", createBasicContext());
        final Section section = song.createSection("A");
        song.getArrangement().addEntry(section, 1);

        section.addPart(GUITAR1, Call.of(RANDOM, 2));

        final Performance performance = song.perform();
        System.out.println(String.format("Performance =>%n%s", performance));

        assertNotNull(performance);
        assertEquals(performance.getSong(), song.getTitle());
        assertEquals(performance.getTracks().size(), 1);
        final Track track = performance.getTracks().iterator().next();
        assertEquals(track.getParty(), GUITAR1.getName());
        // 2 measures of random quarter notes (section A repeats once)
        assertEquals(track.getEvents().size(), 8);
    }
}

