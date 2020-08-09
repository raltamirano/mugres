package mugres.core.function.builtin.song;

import mugres.core.common.*;
import mugres.core.common.chords.ChordProgression;
import mugres.core.function.Call;
import mugres.core.function.Function;
import mugres.core.notation.Section;
import mugres.core.notation.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static mugres.core.common.Context.createBasicContext;
import static mugres.core.common.Direction.ASCENDING;
import static mugres.core.common.Direction.DESCENDING;
import static mugres.core.function.Function.Parameter.Variant.RANDOM;
import static mugres.core.utils.Randoms.*;
import static mugres.core.utils.Utils.rangeClosed;

public class LoFiHipHopSongGenerator extends Function.SongFunction {
    public LoFiHipHopSongGenerator() {
        super("loFiHipHop", "Lo-Fi Hip Hop");
    }

    @Override
    protected Song doExecute(final Context context, final Map<String, Object> arguments) {
        final Song song = Song.of(getDescription() + " song",
                createBasicContext()
                        .setTempo(tempo())
                        .setKey(key())
        );

        final Section prototypeSection = createSongSection("A", song);

        // Alter prototype as a way of making an intro

        // Song's main development
        song.getArrangement().addEntry(prototypeSection, 1);

        // Alter prototype as a way of making a bridge / variation

        // Finale
        //song.getArrangement().addEntry(prototypeSection, 1);

        return song;
    }

    private Section createSongSection(final String name, final Song song) {
        final Section section = song.createSection(name, RND.nextBoolean() ? 4 : 8);
        final ChordProgression chordProgression = improviseChordProgression(section);

        createBeat(section);
        createEPianoChords(chordProgression, section, RND.nextBoolean());

        return section;
    }

    private void createBeat(final Section section) {
        final Map<String, Object> args = new HashMap<>();
        args.put("variant", RANDOM);
        section.addPart(DRUMS, Call.of("hipHopBeat", args));
    }

    private static ChordProgression improviseChordProgression(final Section section) {
        final Scale scale = section.getContext().getKey().defaultScale();
        final List<Integer> scaleDegrees = rangeClosed(1, scale.degrees());
        final List<Integer> roots = randoms(scaleDegrees, 4, false);
        final ChordProgression progression = ChordProgression.of(section.getMeasures());
        final boolean alterChords = section.getMeasures() > 4 || RND.nextBoolean();

        Length at = Length.ZERO;

        if (section.getMeasures() < 4) {
            for (int index = 0; index < section.getMeasures(); index++) {
                progression.event(scale.chordAtDegree(section.getContext().getKey().getRoot(), roots.get(index)), at);
                at = at.plus(section.getContext().getTimeSignature().measuresLength());
            }
        } else if (section.getMeasures() == 4) {
            if (alterChords) {
                for (int index = 0; index < section.getMeasures(); index++) {
                    progression.event(scale.chordAtDegree(section.getContext().getKey().getRoot(), roots.get(index)), at);
                    at = at.plus(section.getContext().getTimeSignature().measuresLength());
                }
            }  else {
                for (int r = 0; r < 2; r++)
                    for (int index = 0; index < 2; index++) {
                        progression.event(scale.chordAtDegree(section.getContext().getKey().getRoot(), roots.get(index)), at);
                        at = at.plus(section.getContext().getTimeSignature().measuresLength());
                    }
            }
        } else {
            for (int index = 0; index < 4; index++) {
                progression.event(scale.chordAtDegree(section.getContext().getKey().getRoot(), roots.get(index)), at);
                at = at.plus(section.getContext().getTimeSignature().measuresLength());
            }

            if (alterChords) {
                final List<Integer> newRoots = randoms(scaleDegrees, 4,  false);
                for (int index = 0; index < newRoots.size(); index++) {
                    progression.event(scale.chordAtDegree(section.getContext().getKey().getRoot(), newRoots.get(index)), at);
                    at = at.plus(section.getContext().getTimeSignature().measuresLength());
                }
            } else {
                // Complete the section length with the same chords as the first part
                final List<ChordProgression.ChordEvent> chordEvents = new ArrayList<>(progression.getEvents());
                for (int index = 0; index < section.getMeasures() - 4; index++) {
                    progression.event(chordEvents.get(index % chordEvents.size()).getChord(), at);
                    at = at.plus(section.getContext().getTimeSignature().measuresLength());
                }
            }
        }

        return progression;
    }

    private void createEPianoChords(final ChordProgression chordProgression, final Section section,
                                    final boolean arpeggiate) {
        final int BASE_OCTAVE = random(asList(2, 3, 4));

        final Direction[] directions = directionsSequence();
        int octave = BASE_OCTAVE;
        final StringBuilder progression = new StringBuilder();
        final List<ChordProgression.ChordEvent> events = chordProgression.getEvents();
        for(int index = 0; index < events.size(); index++) {
            if (index > 0) progression.append("|");

            if (events.get(index).getOctave() != null) {
                octave = events.get(index).getOctave();
            } else {
                final int aux = index % (directions.length + 1);
                if (aux == 0)
                    octave = BASE_OCTAVE;
                else {
                    final Direction direction = directions[aux - 1];
                    final Note lastRoot = events.get(index - 1).getChord().getRoot();
                    final Note currentRoot = events.get(index).getChord().getRoot();

                    if (lastRoot.number() != currentRoot.number()) {
                        if (direction == ASCENDING) {
                            octave = (lastRoot.number() < currentRoot.number()) ? octave : octave + 1;
                        } else {
                            octave = (lastRoot.number() > currentRoot.number()) ? octave : octave - 1;
                        }
                    }
                }
            }

            progression.append(events.get(index).getChord().notation());
            progression.append(" [" + octave + "]");
        }

        final Map<String, Object> args = new HashMap<>();
        args.put("progression", progression.toString());
        Call call = Call.of("chords", args);
        if (arpeggiate) {
            final Map<String, Object> arpArgs = new HashMap<>();
            arpArgs.put("pattern", "4e3e4e3e1h");
            call = call.compose("arp", arpArgs);
        }
        section.addPart(E_PIANO, call);
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

    private static final Party DRUMS = Party.WellKnownParties.DRUMS.getParty();
    private static final Party BASS = Party.WellKnownParties.BASS.getParty();
    private static final Party PAD = Party.WellKnownParties.PAD1.getParty();
    private static final Party E_PIANO = new Party("E-Piano", Instrument.Rhodes_Piano, 3);
    private static final Party MELODY = new Party("Melody", Instrument.Synth_Voice, 4);

    private static final int MIN_TEMPO = 60;
    private static final int MAX_TEMPO = 100;
}
