package mugres.tracker;

import mugres.common.Context;
import mugres.common.Context.ComposableContext;
import mugres.common.DataType;
import mugres.common.Key;
import mugres.common.Length;
import mugres.common.Party;
import mugres.common.TimeSignature;
import mugres.function.Call;
import mugres.function.Function;
import mugres.parametrizable.Parameter;
import mugres.parametrizable.Parametrizable;
import mugres.parametrizable.ParametrizableSupport;

import java.util.*;

import static mugres.common.Context.PATTERN_LENGTH;

public class Pattern implements Parametrizable {
    public static final int MIN_MEASURES = 1;
    public static final int MAX_MEASURES = 10000;
    public static final int MIN_BEAT_SUBDIVISION = 0;
    public static final int MAX_BEAT_SUBDIVISION = 128;

    private String name;
    private final Song song;
    private final Context context;
    private boolean regenerate = false;
    private int beatSubdivision = 0;
    private final Map<Party, List<Call<List<Event>>>> matrix = new HashMap<>();
    private final ParametrizableSupport parametrizableSupport;

    private static final Set<Parameter> PARAMETERS;

    static {
        PARAMETERS = new HashSet<>();

        PARAMETERS.add(Parameter.of("measures", "Measures", 1, "Measures",
                DataType.INTEGER, false, 8, MIN_MEASURES, MAX_MEASURES));
        PARAMETERS.add(Parameter.of("tempo", "BPM" , 2, "BPM",
                DataType.INTEGER, false, 120, Song.MIN_TEMPO, Song.MAX_TEMPO));
        PARAMETERS.add(Parameter.of("key", "Key", 3, "Key",
                DataType.KEY, false, Key.C));
        PARAMETERS.add(Parameter.of("timeSignature", "Time Signature", 4,
                "Time Signature",
                DataType.TIME_SIGNATURE, false, TimeSignature.TS44));
        PARAMETERS.add(Parameter.of("regenerate", "Regenerate?", 5,
                "Whether this pattern should be regenerated every time it's referenced " +
                        "in the Arrangement or not",
                DataType.BOOLEAN, true, false));
        PARAMETERS.add(Parameter.of("beatSubdivision", "Beat subdivision", 6,
                "Beat subdivision", DataType.INTEGER, true,  0, MIN_BEAT_SUBDIVISION,
                MAX_BEAT_SUBDIVISION));
    }

    public Pattern(final Song song, final String name, final int measures) {
        this.song = song;
        this.name = name;
        this.context = ComposableContext.of(song.context());
        this.context.put(PATTERN_LENGTH, measures);

        this.parametrizableSupport = ParametrizableSupport.of(PARAMETERS, this);
    }

    public String name() {
        return name;
    }

    public void name(final String name) {
        this.name = name;
    }

    public Song song() {
        return song;
    }

    public int measures() {
        return context.get(PATTERN_LENGTH);
    }

    public void measures(final int measures) {
        context.put(PATTERN_LENGTH, measures);
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

    public boolean isRegenerate() {
        return regenerate;
    }

    public void setRegenerate(final boolean regenerate) {
        this.regenerate = regenerate;
    }

    public int beatSubdivision() {
        return beatSubdivision;
    }

    public void beatSubdivision(final int beatSubdivision) {
        this.beatSubdivision = beatSubdivision;
    }

    public Map<Party, List<Call<List<Event>>>> matrix() {
        return Collections.unmodifiableMap(matrix);
    }

    public void addPart(final Party.WellKnownParties party, final Call<List<Event>> call) {
        if (party == null)
            throw new IllegalArgumentException("party");

        addPart(party.party(), call);
    }

    public void addPart(final Party party, final Call<List<Event>> call) {
        if (party == null)
            throw new IllegalArgumentException("party");
        if (call == null)
            throw new IllegalArgumentException("call");
        if (!(call.getFunction() instanceof Function.EventsFunction))
            throw new IllegalArgumentException("call must be an instance of " + Function.EventsFunction.class.getName());

        song.addParty(party);
        matrix.computeIfAbsent(party, p -> new ArrayList()).add(call);
    }

    public boolean hasPartsFor(final Party party) {
        return matrix.containsKey(party) && !matrix.get(party).isEmpty();
    }

    public Length length() {
        return context.timeSignature().measuresLength(measures());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return name.equals(pattern.name) &&
                song.equals(pattern.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, song);
    }

    @Override
    public String toString() {
        return "Pattern" +
                "\n{" +
                "\n\tname='" + name + '\'' +
                ",\n\tmeasures=" + measures() +
                ",\n\tsong=" + song.title() +
                ",\n\tcontext=" + context +
                ",\n\tregenerate=" + regenerate +
                ",\n\tbeatSubdivision=" + beatSubdivision +
                ",\n\tmatrix=" + matrix +
                ",\n\tparametrizableSupport=" + parametrizableSupport +
                "\n}";
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
    public Map<String, Object> parameterValues() {
        return parametrizableSupport.parameterValues();
    }
}
