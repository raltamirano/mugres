package mugres.utils;

import mugres.common.Context;
import mugres.common.DrumKit;
import mugres.common.Instrument;
import mugres.common.Interval;
import mugres.common.Note;
import mugres.common.Party;
import mugres.common.Scale;
import mugres.common.Tonality;
import mugres.common.Value;
import mugres.common.euclides.EuclideanPattern;
import mugres.function.Call;
import mugres.function.builtin.arp.Arp2;
import mugres.function.builtin.euclides.Euclides;
import mugres.function.builtin.random.Random;
import mugres.function.builtin.text.TextMelody;
import mugres.tracker.Section;
import mugres.tracker.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static mugres.common.MIDI.PERCUSSION;
import static mugres.utils.Randoms.RND;
import static mugres.utils.Randoms.random;
import static mugres.utils.Randoms.randomBetween;
import static mugres.utils.Utils.toMap;

public class RandomSong {
    private RandomSong() {}

    public static Song randomSong() {
        final Song song = Song.of("Song " + UUID.randomUUID(),
                Context.basicContext().tempo(RND.nextInt(RANDOM_MAX_TEMPO - RANDOM_MIN_TEMPO) + RANDOM_MIN_TEMPO ));

        final List<Party> parties = new ArrayList<>();
        final int numberOfParties = RND.nextInt(RANDOM_MAX_PARTIES) + 1;
        for(int i = 0; i < numberOfParties; i++)
            parties.add(new Party("Party " + i, random(Instrument.values(), Instrument.DrumKit), i));

        final boolean hasPercussion = RND.nextBoolean();
        final boolean percussionAlwaysPresent = RND.nextBoolean();
        final boolean alwaysSamePercussionStyle = RND.nextBoolean();
        final PercussionStyle percussionStyle = alwaysSamePercussionStyle ?
                random(asList(PercussionStyle.values())) : null;
        final Party percussionParty = hasPercussion ?
                new Party("Percussion", Instrument.DrumKit, PERCUSSION) : null;

        final List<Section> sections = new ArrayList<>();
        final int numberOfSections = RND.nextInt(RANDOM_MAX_SECTIONS) + 1;
        for(int i = 0; i < numberOfSections; i++) {
            final Section section = song.createSection("Section " + i, random(RANDOM_SECTIONS_LENGTHS));
            section.context().tempo(RND.nextBoolean() ? section.context().tempo() :
                    RND.nextBoolean() ? section.context().tempo() / 2 : section.context().tempo() * 2);
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
                        section.addPart(party, Call.of("random", section.measures(), randomArguments));
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
                        section.addPart(party, Call.of("textMelody", section.measures(), textMelodyArguments));
                        break;
                    case 2: // Arp
                        final Map<String, Object> arpArguments = toMap(
                                Arp2.PITCHES, actualScale.harmonize(actualRoot, actualRoot, Interval.Type.THIRD,
                                        RANDOM_MAX_ARP_PITCHES, startingOctave),
                                Arp2.PATTERN, randomArpPattern()
                        );
                        section.addPart(party, Call.of("arp2", section.measures(), arpArguments));
                        break;
                    case 3: // Euclidean patterns
                        final List<EuclideanPattern> patterns = new ArrayList<>();
                        for(int i = MIN_EUCLIDES_PATTERNS; i <= MAX_EUCLIDES_PATTERNS; i++)
                            patterns.add(EuclideanPattern.of(EUCLIDES_STEPS,
                                    randomBetween(MIN_EUCLIDES_PATTERN_EVENTS, MAX_EUCLIDES_PATTERN_EVENTS)));

                        final Map<String, Object> euclidesArguments = toMap(
                                Euclides.PATTERNS, patterns,
                                Euclides.SCALE, actualScale,
                                Euclides.STARTING_OCTAVE, startingOctave,
                                Euclides.OCTAVES_TO_GENERATE, octavesToGenerate,
                                Euclides.ROOT, actualRoot,
                                Euclides.CYCLE, section.context().timeSignature().measuresLength(RND.nextBoolean() ? 1 : 2)
                        );
                        section.addPart(party, Call.of("euclides", section.measures(), euclidesArguments));
                        break;
                }
            }

            if (hasPercussion) {
                if (percussionAlwaysPresent || RND.nextBoolean()) {
                    final PercussionStyle style = alwaysSamePercussionStyle ?
                            percussionStyle : random(asList(PercussionStyle.values()));

                    switch (style) {
                        case EUCLIDEAN:
                            final List<EuclideanPattern> patterns = new ArrayList<>();
                            final List<DrumKit> kitPieces = new ArrayList<>();
                            for(int i = MIN_EUCLIDES_PATTERNS; i <= MAX_EUCLIDES_PATTERNS; i++) {
                                patterns.add(EuclideanPattern.of(EUCLIDES_STEPS,
                                        randomBetween(MIN_EUCLIDES_PERCUSSION_PATTERN_EVENTS, MAX_EUCLIDES_PERCUSSION_PATTERN_EVENTS)));
                                kitPieces.add(random(asList(DrumKit.values()), kitPieces));
                            }

                            final Map<String, Object> euclidesArguments = toMap(
                                    Euclides.PATTERNS, patterns,
                                    Euclides.PITCHES, kitPieces.stream().map(DrumKit::pitch).collect(Collectors.toList()),
                                    Euclides.CYCLE, section.context().timeSignature().measuresLength(min((RND.nextBoolean() ? 1 : 2), section.measures()))
                            );
                            section.addPart(percussionParty, Call.of("euclides", section.measures(), euclidesArguments));
                            break;
                    }
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
    private static final int MIN_EUCLIDES_PATTERNS = 2;
    private static final int MAX_EUCLIDES_PATTERNS = 5;
    private static final int MIN_EUCLIDES_PATTERN_EVENTS = 1;
    private static final int MAX_EUCLIDES_PATTERN_EVENTS = 16;
    private static final int MIN_EUCLIDES_PERCUSSION_PATTERN_EVENTS = 1;
    private static final int MAX_EUCLIDES_PERCUSSION_PATTERN_EVENTS = 8;
    private static final int EUCLIDES_STEPS = 16;

    private enum PercussionStyle {
        EUCLIDEAN
    }
}
