package mugres.tracker;

import mugres.common.Context;
import mugres.common.Context.ComposableContext;
import mugres.common.Event;
import mugres.common.Length;
import mugres.common.Party;
import mugres.function.Call;
import mugres.function.Function;

import java.util.*;

import static mugres.common.Context.SECTION_LENGTH;

public class Section {
    private String name;
    private final int measures;
    private final Song song;
    private final Context context;
    private boolean regenerate = false;
    private final Map<Party, List<Call<List<Event>>>> matrix = new HashMap<>();

    public Section(final Song song, final String name, final int measures) {
        this.song = song;
        this.name = name;
        this.measures = measures;
        this.context = ComposableContext.of(song.context());
        this.context.put(SECTION_LENGTH, measures);
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public Song song() {
        return song;
    }

    public int measures() {
        return measures;
    }

    public Context context() {
        return context;
    }

    public boolean isRegenerate() {
        return regenerate;
    }

    public void setRegenerate(boolean regenerate) {
        this.regenerate = regenerate;
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
        return matrix.containsKey(party);
    }

    public Length length() {
        return context.timeSignature().measuresLength(measures);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return name.equals(section.name) &&
                song.equals(section.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, song);
    }

    @Override
    public String toString() {
        return name;
    }
}
