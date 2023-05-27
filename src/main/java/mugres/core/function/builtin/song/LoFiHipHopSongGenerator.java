package mugres.core.function.builtin.song;

import mugres.core.common.Context;
import mugres.core.common.Direction;
import mugres.core.common.Event;
import mugres.core.common.Instrument;
import mugres.core.common.Key;
import mugres.core.common.Length;
import mugres.core.common.Note;
import mugres.core.common.Party;
import mugres.core.common.chords.ChordProgression;
import mugres.core.function.Call;
import mugres.core.function.Function;
import mugres.core.tracker.Section;
import mugres.core.tracker.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static mugres.core.common.Context.basicContext;
import static mugres.core.common.Direction.ASCENDING;
import static mugres.core.common.Direction.DESCENDING;
import static mugres.core.common.chords.Chords.improviseChordProgression;
import static mugres.core.common.Variant.RANDOM;
import static mugres.core.utils.Randoms.RND;
import static mugres.core.utils.Randoms.random;

public class LoFiHipHopSongGenerator extends Function.SongFunction {
    public LoFiHipHopSongGenerator() {
        super("loFiHipHop", "Lo-Fi Hip Hop");
    }

    @Override
    protected Song doExecute(final Context context, final Map<String, Object> arguments) {
        final Song song = Song.of(description() + " song",
                basicContext()
                        .tempo(tempo())
                        .key(key())
        );

        final Section prototypeSection = createSongSection("A", song);

        // Alter prototype as a way of making an intro

        // Song's main development
        song.arrangement().append(prototypeSection, 1);

        // Alter prototype as a way of making a bridge / variation

        // Finale
        //song.getArrangement().addEntry(prototypeSection, 1);

        return song;
    }

    private Section createSongSection(final String name, final Song song) {
        final Section section = song.createSection(name, RND.nextBoolean() ? 4 : 8);
        final ChordProgression chordProgression = improviseChordProgression(section.context(),
                section.measures());
        section.context().chordProgression(chordProgression);

        createBeat(section);
        createEPianoChords(section);
        createLeadMelody(section);

        return section;
    }

    private void createBeat(final Section section) {
        final Map<String, Object> args = new HashMap<>();
        args.put("variant", RANDOM);
        section.addPart(DRUMS, Call.of("hipHopBeat", args));
    }

    private void createEPianoChords(final Section section) {
        final int BASE_OCTAVE = random(asList(2, 3));

        final boolean arpeggiate = RND.nextBoolean();
        final Direction[] directions = directionsSequence();
        int octave = BASE_OCTAVE;
        final StringBuilder progression = new StringBuilder();
        final Map<Length, ChordProgression.ChordEvent> events = section.context().chordProgression().events();
        for(int index = 0; index < events.size(); index++) {
            if (index > 0) progression.append("|");

            final int aux = index % (directions.length + 1);
            if (aux == 0)
                octave = BASE_OCTAVE;
            else {
                final Direction direction = directions[aux - 1];
                final Note lastRoot = events.get(index - 1).chord().root();
                final Note currentRoot = events.get(index).chord().root();

                if (lastRoot.number() != currentRoot.number()) {
                    if (direction == ASCENDING) {
                        octave = (lastRoot.number() < currentRoot.number()) ? octave : octave + 1;
                    } else {
                        octave = (lastRoot.number() > currentRoot.number()) ? octave : octave - 1;
                    }
                }
            }

            progression.append(events.get(index).chord().notation());
            progression.append(" [").append(octave).append("]");
        }

        final Map<String, Object> args = new HashMap<>();
        args.put("progression", progression.toString());
        Call<List<Event>> call = Call.of("chords", args);
        if (arpeggiate) {
            final Map<String, Object> arpArgs = new HashMap<>();
            arpArgs.put("pattern", "1q2q3q3q");
            call = call.compose("arp", arpArgs);
        }
        section.addPart(E_PIANO, call);
    }

    private void createLeadMelody(final Section section) {
        final StringBuilder progression = new StringBuilder();

        boolean first = true;
        for (ChordProgression.ChordEvent c : section.context().chordProgression().events().values()) {
            if (!first) progression.append("|");
            progression.append(c.notation());
            progression.append(" [4]");
            first = false;
        }

        final Map<String, Object> args = new HashMap<>();
        args.put("progression", progression.toString());
        Call<List<Event>> call = Call.of("chords", args);

        final Map<String, Object> arpArgs = new HashMap<>();
        arpArgs.put("pattern", "4e3e4e3eRh 1h3h");
        arpArgs.put("restart", false);
        arpArgs.put("octavesUp", random(asList(0, 1, 2)));
        arpArgs.put("octavesDown", random(asList(0, 1, 2)));
        call = call.compose("arp", arpArgs);
        section.addPart(MELODY, call);
    }

    private static int tempo() {
        return RND.nextInt((MAX_TEMPO - MIN_TEMPO) + 1) + MIN_TEMPO;
    }

    private static Key key() {
        return Key.values()[RND.nextInt(Key.values().length)];
    }

    private static Direction[] directionsSequence() {
        final Direction[] directions = new Direction[3];

        switch(RND.nextInt(6)) {
            case 1: // Always descending
                directions[0] = DESCENDING;
                directions[1] = DESCENDING;
                directions[2] = DESCENDING;
                break;
            case 2: // Variation A
                directions[0] = ASCENDING;
                directions[1] = DESCENDING;
                directions[2] = ASCENDING;
                break;
            case 3: // Variation B
                directions[0] = DESCENDING;
                directions[1] = ASCENDING;
                directions[2] = DESCENDING;
                break;
            case 4: // Variation C
                directions[0] = ASCENDING;
                directions[1] = DESCENDING;
                directions[2] = DESCENDING;
                break;
            case 5: // Variation D
                directions[0] = DESCENDING;
                directions[1] = DESCENDING;
                directions[2] = ASCENDING;
                break;
            default: // Natural order
                directions[0] = null;
                directions[1] = null;
                directions[2] = null;
                break;
        }

        return  directions;
    }

    private static final Party DRUMS = Party.WellKnownParties.DRUMS.party();
    private static final Party BASS = Party.WellKnownParties.BASS.party();
    private static final Party PAD = Party.WellKnownParties.PAD1.party();
    private static final Party E_PIANO = new Party("E-Piano", Instrument.Rhodes_Piano, 3);
    private static final Party MELODY = new Party("Melody", Instrument.Synth_Voice, 4);

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 100;
}
