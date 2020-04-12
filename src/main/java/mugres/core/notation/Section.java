package mugres.core.notation;

import mugres.core.common.Context;
import mugres.core.common.Context.ComposableContext;
import mugres.core.common.Length;
import mugres.core.common.Party;
import mugres.core.function.Call;

import java.util.*;

import static mugres.core.common.Context.SECTION_LENGTH;

public class Section {
    private String name;
    private final int measures;
    private final Song song;
    private final Context context;
    private final Map<Party, List<Call>> matrix = new HashMap<>();

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

    public Map<Party, List<Call>> getMatrix() {
        return Collections.unmodifiableMap(matrix);
    }

    public void addPart(final Party party, final Call call) {
        if (party == null)
            throw new IllegalArgumentException("party");
        if (call == null)
            throw new IllegalArgumentException("call");

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
    public String toString() {
        return name;
    }
}
