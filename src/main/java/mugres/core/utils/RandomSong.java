package mugres.core.utils;

import mugres.core.common.Context;
import mugres.core.common.Instrument;
import mugres.core.common.Interval;
import mugres.core.common.Note;
import mugres.core.common.Party;
import mugres.core.common.Scale;
import mugres.core.common.Tonality;
import mugres.core.common.Value;
import mugres.core.function.Call;
import mugres.core.function.builtin.arp.Arp2;
import mugres.core.function.builtin.euclides.EuclidesPattern;
import mugres.core.function.builtin.random.Random;
import mugres.core.function.builtin.text.TextMelody;
import mugres.core.notation.Section;
import mugres.core.notation.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static mugres.core.common.Value.EIGHTH;
import static mugres.core.common.Value.HALF;
import static mugres.core.common.Value.QUARTER;
import static mugres.core.utils.Randoms.RND;
import static mugres.core.utils.Randoms.random;
import static mugres.core.utils.Randoms.randomBetween;
import static mugres.core.utils.Utils.toMap;

public class RandomSong {
    private RandomSong() {}

    public static Song randomSong() {
        final Song song = Song.of("Song " + UUID.randomUUID(),
                Context.createBasicContext().setTempo(RND.nextInt(RANDOM_MAX_TEMPO - RANDOM_MIN_TEMPO) + RANDOM_MIN_TEMPO ));

        final List<Party> parties = new ArrayList<>();
        final int numberOfParties = RND.nextInt(RANDOM_MAX_PARTIES) + 1;
        for(int i = 0; i < numberOfParties; i++)
            parties.add(new Party("Party " + i, random(Instrument.values(), Instrument.DrumKit), i));

        final List<Section> sections = new ArrayList<>();
        final int numberOfSections = RND.nextInt(RANDOM_MAX_SECTIONS) + 1;
        for(int i = 0; i < numberOfSections; i++) {
            final Section section = song.createSection("Section " + i, random(RANDOM_SECTIONS_LENGTHS));
            section.getContext().setTempo(RND.nextBoolean() ? section.getContext().getTempo() :
                    RND.nextBoolean() ? section.getContext().getTempo() / 2 : section.getContext().getTempo() * 2);
            sections.add(section);
        }

        final boolean useSameRoot = RND.nextBoolean();
        final boolean useSameTonality = RND.nextBoolean();
        final boolean useSameScale = RND.nextBoolean();
        final Tonality tonality = useSameTonality ? random(Tonality.values()) : null;
        final Set<Scale> scales = useSameTonality ? Scale.byTonality(tonality) :
                Arrays.stream(Scale.values()).collect(Collectors.toSet());
        final Scale scale = random(scales);
        final Note root = random(Note.values());

        for(final Section section : sections) {
            for (final Party party : parties) {
                final Note actualRoot = useSameRoot ? root : random(Note.values());
                final Scale actualScale = useSameScale ? scale : random(scales);
                final int startingOctave = random(RANDOM_STARTING_OCTAVE_OPTIONS);
                final int octavesToGenerate = startingOctave < 4 ? random(RANDOM_OCTAVE_TO_GENERATE_OPTIONS) : 1;
                switch(RND.nextInt(4)) {
                    case 0: // Random
                        final Map<String, Object> randomArguments = toMap(
                                Random.SCALE, actualScale,
                                Random.STARTING_OCTAVE, startingOctave,
                                Random.OCTAVES_TO_GENERATE, octavesToGenerate,
                                Random.ROOT, actualRoot
                        );
                        section.addPart(party, Call.of("random", section.getMeasures(), randomArguments));
                        break;
                    case 1: // Text Melody
                        final Map<String, Object> textMelodyArguments = toMap(
                                TextMelody.SCALE, actualScale,
                                TextMelody.STARTING_OCTAVE, startingOctave,
                                TextMelody.OCTAVES_TO_GENERATE, octavesToGenerate,
                                TextMelody.ROOT, actualRoot,
                                TextMelody.SOURCE_TEXT, random(asList(
                                        UUID.randomUUID().toString(),
                                        UUID.randomUUID().toString(),
                                        UUID.randomUUID().toString(),
                                        UUID.randomUUID().toString(),
                                        UUID.randomUUID().toString()))
                        );
                        section.addPart(party, Call.of("textMelody", section.getMeasures(), textMelodyArguments));
                        break;
                    case 2: // Arp
                        final Map<String, Object> arpArguments = toMap(
                                Arp2.PITCHES, actualScale.harmonize(actualRoot, actualRoot, Interval.Type.THIRD,
                                        RANDOM_MAX_ARP_PITCHES, startingOctave),
                                Arp2.PATTERN, randomArpPattern()
                        );
                        section.addPart(party, Call.of("arp2", section.getMeasures(), arpArguments));
                        break;
                    case 3: // Euclidean pattern
                        int steps = randomBetween(4, 32);

                        final Map<String, Object> euclideanArguments = toMap(
                                EuclidesPattern.STEPS, steps,
                                EuclidesPattern.EVENTS, randomBetween(1, steps),
                                EuclidesPattern.OFFSET, randomBetween(0, 4),
                                EuclidesPattern.NOTE_VALUE, random(asList(HALF, QUARTER, EIGHTH)),
                                EuclidesPattern.SCALE, actualScale,
                                EuclidesPattern.STARTING_OCTAVE, startingOctave,
                                EuclidesPattern.OCTAVES_TO_GENERATE, octavesToGenerate,
                                EuclidesPattern.ROOT, actualRoot

                        );
                        section.addPart(party, Call.of("euclides-pattern", section.getMeasures(), euclideanArguments));
                        break;
                }
            }
        }

        switch(numberOfSections) {
            case 1:
                song.arrangement().append(sections.get(0), random(RANDOM_SINGLE_SECTION_REPETITIONS));
                break;
            case 2:
                for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES; i++)
                    song.arrangement().append(sections.get(i % 2), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                break;
            case 3:
                final boolean thirdAsMiddle8 = RND.nextBoolean();
                if (thirdAsMiddle8) {
                    for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES -2; i++)
                        song.arrangement().append(sections.get(i % 2), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                    song.arrangement().append(sections.get(2), random(RANDOM_MIDDLE8_REPETITIONS));
                    song.arrangement().append(sections.get(0), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                } else {
                    for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES; i++)
                        song.arrangement().append(sections.get(i % 3), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                }
                for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES; i++)
                    song.arrangement().append(sections.get(i % 2), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                break;
        }

        return song;
    }

    private static String randomArpPattern() {
        if (RND.nextBoolean())
            return random(asList(
                    "12", "13", "123", "1232", "1234", "123432",
                    "1e2e", "1e3e", "1e2e3e", "1e2e3e2", "1e2e3e4e", "1e2e3e4e3e2e"
            ));

        final List<String> steps = new ArrayList<>();
        for(int i=0; i<RANDOM_MAX_ARP_PITCHES; i++) {
            final int index = i == 0 ? 1 : RND.nextInt(RANDOM_MAX_ARP_PITCHES) + 1;
            final String duration = random(Value.values()).id();
        }

        return String.join("", steps);
    }

    private static final int RANDOM_MAX_PARTIES = 5;
    private static final int RANDOM_MAX_SECTIONS = 3;
    private static final Set<Integer> RANDOM_SECTIONS_LENGTHS = new HashSet<>(asList(8, 16, 32 ));
    private static final Set<Integer> RANDOM_SINGLE_SECTION_REPETITIONS = new HashSet<>(asList( 8, 12, 16, 20, 24 ));
    private static final Set<Integer> RANDOM_MIDDLE8_REPETITIONS = new HashSet<>(asList( 1, 2, 4 ));
    private static final int RANDOM_BASIC_ARRANGEMENT_ENTRIES = 8;
    private static final Set<Integer> RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS = new HashSet<>(asList( 1, 2 ));
    private static final Set<Integer> RANDOM_STARTING_OCTAVE_OPTIONS = new HashSet<>(asList( 1, 2, 3, 4, 5 ));
    private static final Set<Integer> RANDOM_OCTAVE_TO_GENERATE_OPTIONS = new HashSet<>(asList( 1, 2, 3 ));
    private static final int RANDOM_MIN_TEMPO = 20;
    private static final int RANDOM_MAX_TEMPO = 200;
    private static final int RANDOM_MAX_ARP_PITCHES = 5;
}
