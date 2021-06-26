package mugres.core.notation;

import mugres.core.common.Context;
import mugres.core.common.Context.ComposableContext;
import mugres.core.common.Event;
import mugres.core.common.Length;
import mugres.core.common.Party;
import mugres.core.function.Call;
import mugres.core.function.Function;

import java.util.*;

import static mugres.core.common.Context.SECTION_LENGTH;

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
        this.context = ComposableContext.of(song.getContext());
        this.context.put(SECTION_LENGTH, measures);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Song getSong() {
        return song;
    }

    public int getMeasures() {
        return measures;
    }

    public Context getContext() {
        return context;
    }

    public boolean isRegenerate() {
        return regenerate;
    }

    public void setRegenerate(boolean regenerate) {
        this.regenerate = regenerate;
    }

    public Map<Party, List<Call<List<Event>>>> getMatrix() {
        return Collections.unmodifiableMap(matrix);
    }

    public void addPart(final Party.WellKnownParties party, final Call<List<Event>> call) {
        if (party == null)
            throw new IllegalArgumentException("party");

        addPart(party.getParty(), call);
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

    public Length getLength() {
        return context.getTimeSignature().measuresLength(measures);
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
