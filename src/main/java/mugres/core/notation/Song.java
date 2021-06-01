package mugres.core.notation;

import mugres.core.common.Context;
import mugres.core.common.Event;
import mugres.core.common.Instrument;
import mugres.core.common.Note;
import mugres.core.common.Party;
import mugres.core.common.Scale;
import mugres.core.common.Tonality;
import mugres.core.function.Call;
import mugres.core.function.builtin.random.Random;
import mugres.core.function.builtin.text.TextMelody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static mugres.core.utils.Randoms.RND;
import static mugres.core.utils.Randoms.random;
import static mugres.core.utils.Utils.toMap;

/** MUGRES internal representation of a song. */
public class Song {
    private String title;
    private final Context context;
    private final Set<Section> sections = new HashSet<>();
    private final Set<Party> parties = new HashSet<>();
    private final Arrangement arrangement = new Arrangement();

    private Song(final String title, final Context context) {
        this.title = title;
        this.context = context;
    }

    public static Song of(final String title, final Context context) {
        return new Song(title, context);
    }

    public static Song of(final Context functionCallsContext,
                          final Party functionCallsParty,
                          final Call<List<Event>> call) {
        final Song functionCallSong = new Song("Untitled", functionCallsContext);
        final Section section = functionCallSong.createSection("A", call.getLengthInMeasures());
        section.addPart(functionCallsParty, call);
        functionCallSong.arrangement.addEntry(section, 1);
        return functionCallSong;
    }

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

        final boolean useSameTonality = RND.nextBoolean();
        final boolean useSameScale = RND.nextBoolean();
        final Tonality tonality = useSameTonality ? random(Tonality.values()) : null;
        final Set<Scale> scales = useSameTonality ? Scale.byTonality(tonality) :
                Arrays.stream(Scale.values()).collect(Collectors.toSet());
        final Scale scale = random(scales);

        for(final Section section : sections) {
            for (final Party party : parties) {
                final int startingOctave = random(RANDOM_STARTING_OCTAVE_OPTIONS);
                final int octavesToGenerate = startingOctave < 4 ? random(RANDOM_OCTAVE_TO_GENERATE_OPTIONS) : 1;
                if (RND.nextBoolean()) {
                    final Map<String, Object> arguments = toMap(
                            Random.SCALE, useSameScale ? scale : random(scales),
                            Random.STARTING_OCTAVE, startingOctave,
                            Random.OCTAVES_TO_GENERATE, octavesToGenerate,
                            Random.ROOT, random(Note.values())
                    );
                    section.addPart(party, Call.of("random", section.getMeasures(), arguments));
                } else {
                    final Map<String, Object> arguments = toMap(
                            TextMelody.SCALE, useSameScale ? scale : random(scales),
                            TextMelody.STARTING_OCTAVE, startingOctave,
                            TextMelody.OCTAVES_TO_GENERATE, octavesToGenerate,
                            TextMelody.ROOT, random(Note.values()),
                            TextMelody.SOURCE_TEXT, random(asList(
                                    UUID.randomUUID().toString(),
                                    UUID.randomUUID().toString(),
                                    UUID.randomUUID().toString(),
                                    UUID.randomUUID().toString(),
                                    UUID.randomUUID().toString()))
                    );
                    section.addPart(party, Call.of("textMelody", section.getMeasures(), arguments));
                }
            }
        }

        switch(numberOfSections) {
            case 1:
                song.getArrangement().addEntry(sections.get(0), random(RANDOM_SINGLE_SECTION_REPETITIONS));
                break;
            case 2:
                for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES; i++)
                    song.getArrangement().addEntry(sections.get(i % 2), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                break;
            case 3:
                final boolean thirdAsMiddle8 = RND.nextBoolean();
                if (thirdAsMiddle8) {
                    for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES -2; i++)
                        song.getArrangement().addEntry(sections.get(i % 2), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                    song.getArrangement().addEntry(sections.get(2), random(RANDOM_MIDDLE8_REPETITIONS));
                    song.getArrangement().addEntry(sections.get(0), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                } else {
                    for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES; i++)
                        song.getArrangement().addEntry(sections.get(i % 3), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                }
                for(int i = 0; i < RANDOM_BASIC_ARRANGEMENT_ENTRIES; i++)
                    song.getArrangement().addEntry(sections.get(i % 2), random(RANDOM_BASIC_ARRANGEMENT_SECTION_REPETITIONS));
                break;
        }

        return song;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Context getContext() {
        return context;
    }

    public Section createSection(final String sectionName, final int measures) {
        if (sections.stream().anyMatch(s -> s.getName().equals(sectionName)))
            throw new IllegalArgumentException(String.format("Section '%s' already exists!", sectionName));

        final Section section = new Section(this, sectionName, measures);
        sections.add(section);
        return section;
    }

    /** Creates a song that contains a single section from this songs. That section will be arranged
     * to be repeated once. */
    public Song createSectionSong(final String sectionName) {
        final Section section = getSection(sectionName);
        if (section == null)
            throw new IllegalArgumentException("Unknown section: " + sectionName);

        final Song sectionSong = Song.of(sectionName, context);
        sectionSong.parties.addAll(parties);
        sectionSong.sections.add(section);
        sectionSong.arrangement.addEntry(section, 1);

        return sectionSong;
    }

    void addParty(final Party party) {
        if (party == null)
            throw new IllegalArgumentException("party");

        parties.add(party);
    }

    public Set<Section> getSections() {
        return Collections.unmodifiableSet(sections);
    }

    public Section getSection(final String name) {
        for(Section section : sections)
            if (section.getName().equals(name))
                return section;

        return null;
    }

    public Set<Party> getParties() {
        return Collections.unmodifiableSet(parties);
    }

    public Arrangement getArrangement() {
        return arrangement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return title.equals(song.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    private static final int RANDOM_MAX_PARTIES = 4;
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
}
