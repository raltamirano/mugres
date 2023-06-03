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
    private final Set<Pattern> patterns = new HashSet<>();
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
        final Pattern pattern = functionCallSong.createPattern("A", call.getLengthInMeasures());
        pattern.addPart(functionCallsParty, call);
        functionCallSong.arrangement.append(pattern, 1);
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

    public Pattern createPattern(final String patternName, final int measures) {
        if (patterns.stream().anyMatch(s -> s.name().equals(patternName)))
            throw new IllegalArgumentException(String.format("Pattern '%s' already exists!", patternName));

        final Pattern pattern = new Pattern(this, patternName, measures);
        patterns.add(pattern);
        return pattern;
    }

    /** Creates a song that contains a single pattern from this song. That pattern will be arranged
     * to be repeated once. */
    public Song createPatternSong(final String patternName) {
        final Pattern pattern = pattern(patternName);
        if (pattern == null)
            throw new IllegalArgumentException("Unknown pattern: " + patternName);

        final Song patternSong = Song.of(patternName, context);
        patternSong.parties.addAll(parties);
        patternSong.patterns.add(pattern);
        patternSong.arrangement.append(pattern, 1);

        return patternSong;
    }

    void addParty(final Party party) {
        if (party == null)
            throw new IllegalArgumentException("party");

        parties.add(party);
    }

    public Set<Pattern> patterns() {
        return Collections.unmodifiableSet(patterns);
    }

    public Pattern pattern(final String name) {
        for(Pattern pattern : patterns)
            if (pattern.name().equals(name))
                return pattern;

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
