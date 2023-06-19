package mugres.tracker;

import mugres.MUGRES;
import mugres.common.Context;
import mugres.common.DataType;
import mugres.common.Key;
import mugres.common.Party;
import mugres.common.TimeSignature;
import mugres.function.Call;
import mugres.parametrizable.Parameter;
import mugres.parametrizable.Parametrizable;
import mugres.parametrizable.ParametrizableSupport;
import mugres.tracker.performance.Performance;
import mugres.tracker.performance.Performer;
import mugres.tracker.performance.converters.ToMidiSequenceConverter;

import javax.sound.midi.Sequence;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static mugres.utils.ObjectMapping.mapToPojo;

/** MUGRES internal representation of a song. */
public class Song implements Parametrizable {
    public static final Object MIN_TEMPO = 1;
    public static final Object MAX_TEMPO = 10000;

    private String title;
    private final Context context;
    private final Map<String, Object> metadata;
    private final Set<Pattern> patterns = new HashSet<>();
    private final Set<Party> parties = new HashSet<>();
    private final Arrangement arrangement = new Arrangement();
    private final ParametrizableSupport parametrizableSupport;

    private static final Set<Parameter> PARAMETERS;

    static {
        PARAMETERS = new HashSet<>();

        PARAMETERS.add(Parameter.of("title", "Title", 1, "Title",
                DataType.TEXT, false, "Untitled"));
        PARAMETERS.add(Parameter.of("tempo", "BPM", 2, "BPM",
                DataType.INTEGER, false,120, MIN_TEMPO, MAX_TEMPO));
        PARAMETERS.add(Parameter.of("key", "Key", 3, "Key",
                DataType.KEY, false, Key.C));
        PARAMETERS.add(Parameter.of("timeSignature", "Time Signature", 4,
                "Time Signature", DataType.TIME_SIGNATURE, false, TimeSignature.TS44));
    }

    private Song(final String title, final Context context, final Map<String, Object> metadata) {
        this.title = title;
        this.context = context;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();

        this.parametrizableSupport = ParametrizableSupport.forTarget(PARAMETERS, this);
    }

    public static Song of(final String title, final Context context) {
        return new Song(title, context, null);
    }

    public static Song of(final String title, final Context context, final Map<String, Object> metadata) {
        return new Song(title, context, metadata);
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
        final Song functionCallSong = new Song("Untitled", functionCallsContext, null);
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

    public int tempo() {
        return context.tempo();
    }

    public void tempo(final int tempo) {
        context.tempo(tempo);
    }

    public Key key() {
        return context.key();
    }

    public void key(final Key key) {
        context.key(key);
    }

    public TimeSignature timeSignature() {
        return context.timeSignature();
    }

    public void timeSignature(final TimeSignature timeSignature) {
        context.timeSignature(timeSignature);
    }

    public Context context() {
        return context;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public <X> X metadataAs(final Class<X> clazz) {
        return mapToPojo(metadata, clazz);
    }

    public <X> X metadataAs(final String key, final Class<X> clazz) {
        return mapToPojo((Map<String, Object>) metadata.get(key), clazz);
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
    public String toString() {
        return "Song" +
                "\n{" +
                "\n\ttitle='" + title + '\'' +
                ",\n\tcontext=" + context +
                ",\n\tmetadata=" + metadata +
                ",\n\tpatterns=" + patterns +
                ",\n\tparties=" + parties +
                ",\n\tarrangement=" + arrangement +
                "\n}";
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

    @Override
    public Set<Parameter> parameters() {
        return parametrizableSupport.parameters();
    }

    @Override
    public Parameter parameter(final String name) {
        return parametrizableSupport.parameter(name);
    }

    @Override
    public void parameterValue(final String name, Object value) {
        parametrizableSupport.parameterValue(name, value);
    }

    @Override
    public Object parameterValue(final String name) {
        return parametrizableSupport.parameterValue(name);
    }

    @Override
    public boolean hasParameterValue(final String name) {
        if (Context.MAIN_PROPERTIES.contains(name))
            return context.overrides(name);
        else
            return parametrizableSupport.hasParameterValue(name);
    }

    @Override
    public Map<String, Object> parameterValues() {
        return parametrizableSupport.parameterValues();
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        parametrizableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        parametrizableSupport.removePropertyChangeListener(listener);
    }
}
