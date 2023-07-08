package mugres.tracker;

import mugres.common.Context;
import mugres.common.Context.ComposableContext;
import mugres.common.DataType;
import mugres.common.Key;
import mugres.common.Length;
import mugres.common.TimeSignature;
import mugres.function.Call;
import mugres.function.Function;
import mugres.parametrizable.Parameter;
import mugres.parametrizable.ParametrizableSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static mugres.common.Context.MEASURES;

public class Pattern extends TrackerElement {
    public static final int MIN_MEASURES = 1;
    public static final int MAX_MEASURES = 10000;

    private final Song song;
    private boolean regenerate = false;
    private final Map<Track, List<Call<List<Event>>>> matrix = new HashMap<>();
    private static final Set<Parameter> PARAMETERS;

    static {
        PARAMETERS = new HashSet<>();

        PARAMETERS.add(Parameter.of("name", "Name", 1, "Pattern name",
                DataType.TEXT, false));
        PARAMETERS.add(Parameter.of("measures", "Measures", 2, "Measures",
                DataType.INTEGER, false, 8, MIN_MEASURES, MAX_MEASURES, false));
        PARAMETERS.add(Parameter.of(Context.TEMPO, "BPM" , 3, "BPM",
                DataType.INTEGER, false, 120, Song.MIN_TEMPO, Song.MAX_TEMPO, true));
        PARAMETERS.add(Parameter.of(Context.KEY, "Key", 4, "Key",
                DataType.KEY, false, Key.C, false, true));
        PARAMETERS.add(Parameter.of(Context.TIME_SIGNATURE, "Time Signature", 5,
                "Time Signature",
                DataType.TIME_SIGNATURE, false, TimeSignature.TS44, false, true));
        PARAMETERS.add(Parameter.of("regenerate", "Regenerate?", 6,
                "Whether this pattern should be regenerated every time it's referenced " +
                        "in the Arrangement or not",
                DataType.BOOLEAN, true, false, false, false));
    }

    private Pattern(final UUID id, final Song song, final String name, final int measures) {
        super(id, name, ComposableContext.of(song.context()));

        this.song = song;

        context().put(MEASURES, measures);
    }

    public static Pattern of(final UUID id, final Song song, final String name, final int measures) {
        return new Pattern(id, song, name, measures);
    }

    @Override
    protected ParametrizableSupport createParametrizableSupport() {
        final ParametrizableSupport parametrizableSupport = ParametrizableSupport.forTarget(PARAMETERS, this, song);
        parametrizableSupport.setCustomHasParameterValueLogic(p ->
                Context.MAIN_PROPERTIES.contains(p) ? context().overrides(p) : null);
        return parametrizableSupport;
    }

    public Song song() {
        return song;
    }

    public int measures() {
        return context().get(MEASURES);
    }

    public void measures(final int measures) {
        final int oldValue = measures();
        context().put(MEASURES, measures);
        propertyChangeSupport().firePropertyChange(MEASURES, oldValue, measures);
    }

    public boolean isRegenerate() {
        return regenerate;
    }

    public void setRegenerate(final boolean regenerate) {
        this.regenerate = regenerate;
    }

    public Map<Track, List<Call<List<Event>>>> matrix() {
        return Collections.unmodifiableMap(matrix);
    }

    /**
     * Returns the first function call for the given track on the matrix for this pattern, if any.
     */
    public Call<List<Event>> matrix(final Track track) {
        final List<Call<List<Event>>> calls = matrix.get(track);
        return (calls == null || calls.isEmpty()) ? null : calls.get(0);
    }

    /**
     * Sets the first function call for the given track on the matrix for this pattern.
     */
    public void matrix(final Track track, final Call<List<Event>> call) {
        final List<Call<List<Event>>> calls = validateTrackAndCallBeforeEditingCalls(track, call);
        if (calls.isEmpty())
            calls.add(call);
        else
            calls.set(0, call);
    }

    public void addPart(final Track.WellKnownTracks track, final Call<List<Event>> call) {
        if (track == null)
            throw new IllegalArgumentException("track");

        addPart(track.track(), call);
    }

    public void addPart(final Track track, final Call<List<Event>> call) {
        validateTrackAndCallBeforeEditingCalls(track, call).add(call);
    }

    public void removePartsFor(final Track track) {
        if (track == null)
            throw new IllegalArgumentException("track");

        matrix.remove(track);
    }

    public boolean hasPartsFor(final Track track) {
        return matrix.containsKey(track) && !matrix.get(track).isEmpty();
    }

    public Length length() {
        return context().timeSignature().measuresLength(measures());
    }

    private List<Call<List<Event>>> validateTrackAndCallBeforeEditingCalls(Track track, Call<List<Event>> call) {
        if (track == null)
            throw new IllegalArgumentException("track");
        if (call == null)
            throw new IllegalArgumentException("call");
        if (!(call.getFunction() instanceof Function.EventsFunction))
            throw new IllegalArgumentException("Call's function must be an instance of " + Function.EventsFunction.class.getName());

        if (!song.tracks().contains(track))
            song.addTrack(track);

        return matrix.computeIfAbsent(track, p -> new ArrayList());
    }

    @Override
    public String toString() {
        return "Pattern" +
                "\n{" +
                "\n\tid=" + id() +
                ",\n\tname='" + name() + '\'' +
                ",\n\tmeasures=" + measures() +
                ",\n\tsong=" + song.name() +
                ",\n\tcontext=" + context() +
                ",\n\tregenerate=" + regenerate +
                ",\n\tmatrix=" + matrix +
                "\n}";
    }
}
