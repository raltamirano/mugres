package mugres.common;

import mugres.common.chords.ChordProgression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

/** Context data for all things musical. */
public interface Context {
    default int tempo() { return get(TEMPO); }
    default Context tempo(final int tempo) { put(TEMPO, tempo); return this;}
    default Key key() { return get(KEY); }
    default Context key(final Key key) { put(KEY, key); return this; }
    default TimeSignature timeSignature() { return get(TIME_SIGNATURE); }
    default Context timeSignature(final TimeSignature timeSignature) { put(TIME_SIGNATURE, timeSignature); return this; }
    default ChordProgression chordProgression() { return get(CHORD_PROGRESSION); }
    default Context chordProgression(final ChordProgression chordProgression) { put(CHORD_PROGRESSION, chordProgression); return this; }

    void put(final String key, Object value);
    <X> X get(final String key);
    boolean has(final String key);

    static Context basicContext() {
        final Context context = new Context.ComposableContext();
        context.put(Context.TEMPO, 120);
        context.put(Context.KEY, Key.C);
        context.put(Context.TIME_SIGNATURE, TimeSignature.TS44);
        return context;
    }

    String TEMPO = "tempo";
    String KEY = "key";
    String TIME_SIGNATURE = "time-signature";
    String SECTION_LENGTH = "section-length";
    String CHORD_PROGRESSION = "chord-progression";

    final class ComposableContext implements Context
    {
        private final Map<String, Object> data = new HashMap<>();
        private final Set<Context> parents = new HashSet<>();

        private ComposableContext(final Context... parents) {
            this.parents.addAll(asList(parents));
        }

        public static ComposableContext of(final Context... parents) {
            return new ComposableContext(parents);
        }

        @Override
        public void put(final String key, final Object value) {
            this.data.put(key, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <X> X get(final String key) {
            Object found = data.get(key);
            if (found == null) {
                for(Context parent : parents) {
                    found = parent.get(key);
                    if (found != null)
                        break;
                }
            }

            return (X)found;
        }

        @Override
        public boolean has(final String key) {
            if (data.containsKey(key))
                return true;

            for(Context parent : parents)
                if (parent.has(key))
                    return true;

            return false;
        }
    }
}
