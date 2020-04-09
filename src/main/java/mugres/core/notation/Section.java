package mugres.core.notation;

import mugres.core.function.Call;
import mugres.core.common.Context;
import mugres.core.common.Context.ComposableContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Section {
    private String name;
    private Song song;
    private final Context context;
    private final Map<Party, List<Call>> matrix = new HashMap<>();

    public Section(final Song song, final String name) {
        this.song = song;
        this.name = name;
        this.context = new ComposableContext(song.getContext());
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

    public Context getContext() {
        return context;
    }

    public Map<Party, List<Call>> getMatrix() {
        return Collections.unmodifiableMap(matrix);
    }

    @Override
    public String toString() {
        return name;
    }
}
