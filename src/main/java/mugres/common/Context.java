package mugres.common;

import mugres.common.chords.ChordProgression;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    boolean overrides(final String key);
    void undoOverride(final String key);

    void addPropertyChangeListener(final PropertyChangeListener listener);
    void removePropertyChangeListener(final PropertyChangeListener listener);

    static Context basicContext() {
        final Context context = Context.ComposableContext.of();
        context.put(Context.TEMPO, 120);
        context.put(Context.KEY, Key.C);
        context.put(Context.TIME_SIGNATURE, TimeSignature.TS44);
        return context;
    }

    String TEMPO = "tempo";
    String KEY = "key";
    String TIME_SIGNATURE = "timeSignature";
    String MEASURES = "measures";
    String CHORD_PROGRESSION = "chordProgression";
    Set<String> MAIN_PROPERTIES = new HashSet<>(asList(TEMPO, KEY, TIME_SIGNATURE, CHORD_PROGRESSION));

    final class ComposableContext implements Context
    {
        private final Map<String, Object> data = new HashMap<>();
        private final Set<Context> parents = new HashSet<>();
        private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
        private final PropertyChangeListener propertyChangeListener = createPropertyChangeListener();

        private ComposableContext(final Context... parents) {
            final List<Context> parentContexts = asList(parents);
            this.parents.addAll(parentContexts);
            parentContexts.forEach(p -> p.addPropertyChangeListener(propertyChangeListener));
        }

        public static ComposableContext of(final Context... parents) {
            return new ComposableContext(parents);
        }

        @Override
        public void addPropertyChangeListener(final PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(final PropertyChangeListener listener) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        @Override
        public void put(final String key, final Object value) {
            final Object oldValue = get(key);
            if (value == null || TEMPO.equals(key) && ((int)value) == 0)
                this.data.remove(key);
            else
                this.data.put(key, value);
            propertyChangeSupport.firePropertyChange(key, oldValue, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <X> X get(final String key) {
            if (data.containsKey(key))
                return (X)data.get(key);

            for(final Context parent : parents)
                if (parent.has(key))
                    return parent.get(key);

            return null;
        }

        @Override
        public boolean has(final String key) {
            if (data.containsKey(key))
                return true;

            for(final Context parent : parents)
                if (parent.has(key))
                    return true;

            return false;
        }

        @Override
        public boolean overrides(final String key) {
            return data.containsKey(key);
        }

        @Override
        public void undoOverride(final String key) {
            if (overrides(key)) {
                final Object oldValue = get(key);
                data.remove(key);
                propertyChangeSupport.firePropertyChange(key, oldValue, get(key));
            }
        }

        @Override
        public String toString() {
            return "ComposableContext{" +
                    "data=" + data +
                    ", parents=" + parents +
                    '}';
        }

        private PropertyChangeListener createPropertyChangeListener() {
            return e -> {
                if (!overrides(e.getPropertyName()))
                    propertyChangeSupport.firePropertyChange(e.getPropertyName(), e.getOldValue(), e.getNewValue());
            };
        }
    }
}
