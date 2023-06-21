package mugres.function.builtin.song;

import mugres.common.Context;
import mugres.common.Direction;
import mugres.common.Instrument;
import mugres.common.Key;
import mugres.common.Length;
import mugres.common.Note;
import mugres.tracker.Track;
import mugres.common.chords.ChordProgression;
import mugres.function.Call;
import mugres.function.Function;
import mugres.tracker.Event;
import mugres.tracker.Pattern;
import mugres.tracker.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static mugres.common.Context.basicContext;
import static mugres.common.Direction.ASCENDING;
import static mugres.common.Direction.DESCENDING;
import static mugres.common.Variant.RANDOM;
import static mugres.common.chords.Chords.improviseChordProgression;
import static mugres.utils.Randoms.RND;
import static mugres.utils.Randoms.random;

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

        final Pattern prototypePattern = createSongPattern("A", song);

        // Alter prototype as a way of making an intro

        // Song's main development
        song.arrangement().append(prototypePattern, 1);

        // Alter prototype as a way of making a bridge / variation

        // Finale
        //song.getArrangement().addEntry(prototypePattern, 1);

        return song;
    }

    private Pattern createSongPattern(final String name, final Song song) {
        final Pattern pattern = song.createPattern(name, RND.nextBoolean() ? 4 : 8);
        final ChordProgression chordProgression = improviseChordProgression(pattern.context(),
                pattern.measures());
        pattern.context().chordProgression(chordProgression);

        createBeat(pattern);
        createEPianoChords(pattern);
        createLeadMelody(pattern);

        return pattern;
    }

    private void createBeat(final Pattern pattern) {
        final Map<String, Object> args = new HashMap<>();
        args.put("variant", RANDOM);
        pattern.addPart(DRUMS, Call.of("hipHopBeat", args));
    }

    private void createEPianoChords(final Pattern pattern) {
        final int BASE_OCTAVE = random(asList(2, 3));

        final boolean arpeggiate = RND.nextBoolean();
        final Direction[] directions = directionsSequence();
        int octave = BASE_OCTAVE;
        final StringBuilder progression = new StringBuilder();
        final Map<Length, ChordProgression.ChordEvent> events = pattern.context().chordProgression().events();
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
        pattern.addPart(E_PIANO, call);
    }

    private void createLeadMelody(final Pattern pattern) {
        final StringBuilder progression = new StringBuilder();

        boolean first = true;
        for (ChordProgression.ChordEvent c : pattern.context().chordProgression().events().values()) {
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
        pattern.addPart(MELODY, call);
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

    private static final Track DRUMS = Track.WellKnownTracks.DRUMS.track();
    private static final Track BASS = Track.WellKnownTracks.BASS.track();
    private static final Track PAD = Track.WellKnownTracks.PAD1.track();
    private static final Track E_PIANO = Track.of("E-Piano", Instrument.Rhodes_Piano, 3);
    private static final Track MELODY = Track.of("Melody", Instrument.Synth_Voice, 4);

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 100;
}
