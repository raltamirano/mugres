package mugres.core.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

/** Context data for all things musical. */
public interface Context {
    default int getTempo() { return get(TEMPO); }
    default Key getKey() { return get(KEY); }
    default TimeSignature getTimeSignature() { return get(TIME_SIGNATURE); }

    void put(final String key, Object value);
    <X> X get(final String key);

    String TEMPO = "tempo";
    String KEY = "key";
    String TIME_SIGNATURE = "time-signature";

    final class ComposableContext implements Context
    {
        private final Map<String, Object> data = new HashMap<>();
        private final Set<Context> parents = new HashSet<>();

        public ComposableContext(final Context... parents) {
            this.parents.addAll(asList(parents));
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
    }
}
