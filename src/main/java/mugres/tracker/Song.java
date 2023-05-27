package mugres.tracker;

import mugres.MUGRES;
import mugres.common.Context;
import mugres.common.Party;
import mugres.function.Call;
import mugres.tracker.performance.Performance;
import mugres.tracker.performance.Performer;
import mugres.tracker.performance.converters.ToMidiSequenceConverter;

import javax.sound.midi.Sequence;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public static Song of(final Call<List<Event>> call) {
        return of(Context.basicContext(), Party.WellKnownParties.PIANO, call);
    }

    public static Song of(final Party.WellKnownParties functionCallsParty,
                          final Call<List<Event>> call) {
        return of(Context.basicContext(), functionCallsParty.party(), call);
    }

    public static Song of(final Context functionCallsContext,
                          final Call<List<Event>> call) {
        return of(functionCallsContext,Party.WellKnownParties.PIANO.party(), call);
    }

    public static Song of(final Party functionCallsParty,
                          final Call<List<Event>> call) {
        return of(Context.basicContext(), functionCallsParty, call);
    }

    public static Song of(final Context functionCallsContext,
                          final Party.WellKnownParties functionCallsParty,
                          final Call<List<Event>> call) {
            return of(functionCallsContext, functionCallsParty.party(), call);
    }

    public static Song of(final Context functionCallsContext,
                          final Party functionCallsParty,
                          final Call<List<Event>> call) {
        final Song functionCallSong = new Song("Untitled", functionCallsContext);
        final Section section = functionCallSong.createSection("A", call.getLengthInMeasures());
        section.addPart(functionCallsParty, call);
        functionCallSong.arrangement.append(section, 1);
        return functionCallSong;
    }

    public String title() {
        return title;
    }

    public void title(final String title) {
        this.title = title;
    }

    public Context context() {
        return context;
    }

    public Section createSection(final String sectionName, final int measures) {
        if (sections.stream().anyMatch(s -> s.name().equals(sectionName)))
            throw new IllegalArgumentException(String.format("Section '%s' already exists!", sectionName));

        final Section section = new Section(this, sectionName, measures);
        sections.add(section);
        return section;
    }

    /** Creates a song that contains a single section from this songs. That section will be arranged
     * to be repeated once. */
    public Song createSectionSong(final String sectionName) {
        final Section section = section(sectionName);
        if (section == null)
            throw new IllegalArgumentException("Unknown section: " + sectionName);

        final Song sectionSong = Song.of(sectionName, context);
        sectionSong.parties.addAll(parties);
        sectionSong.sections.add(section);
        sectionSong.arrangement.append(section, 1);

        return sectionSong;
    }

    void addParty(final Party party) {
        if (party == null)
            throw new IllegalArgumentException("party");

        parties.add(party);
    }

    public Set<Section> sections() {
        return Collections.unmodifiableSet(sections);
    }

    public Section section(final String name) {
        for(Section section : sections)
            if (section.name().equals(name))
                return section;

        return null;
    }

    public Set<Party> parties() {
        return Collections.unmodifiableSet(parties);
    }

    public Arrangement arrangement() {
        return arrangement;
    }

    public Sequence toMidiSequence() {
        final Performance performance = Performer.perform(this);
        return ToMidiSequenceConverter.getInstance().convert(performance);
    }

    public void play() {
        MUGRES.output().send(this);
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
}
