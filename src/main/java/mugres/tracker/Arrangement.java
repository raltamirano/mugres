package mugres.tracker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Arrangement {
    public static final String ENTRIES = "entries";

    private final List<Entry> entries = new ArrayList<>();
    private final PropertyChangeSupport propertyChangeSupport;

    private Arrangement() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public static Arrangement of() {
        return new Arrangement();
    }

    public void append(final Pattern pattern, final int repetitions) {
        entries.add(new Entry(pattern, repetitions));
        propertyChangeSupport.firePropertyChange(ENTRIES, null, entries());
    }

    public void remove(final int index) {
        if (index < 0 || index >= entries.size())
            return;

        entries.remove(index);
        propertyChangeSupport.firePropertyChange(ENTRIES, null, entries());
    }

    public void moveBack(final int index) {
        if (index <= 0 || index >= entries.size())
            return;

        Collections.swap(entries, index, index - 1);
        propertyChangeSupport.firePropertyChange(ENTRIES, null, entries());
    }

    public void moveForward(final int index) {
        if (index < 0 || index >= entries.size() - 1)
            return;

        Collections.swap(entries, index, index + 1);
        propertyChangeSupport.firePropertyChange(ENTRIES, null, entries());
    }

    public boolean removeAllForPattern(Pattern toRemove) {
        final boolean removed = entries.removeIf(e -> e.pattern().equals(toRemove));
        if (removed)
            propertyChangeSupport.firePropertyChange(ENTRIES, null, entries());
        return removed;
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Arrangement{" +
                "entries=" + entries +
                '}';
    }

    public static class Entry {
        private Pattern pattern;
        private int repetitions;

        public Entry(Pattern pattern, int repetitions) {
            this.pattern = pattern;
            this.repetitions = repetitions;
        }

        public Pattern pattern() {
            return pattern;
        }

        public void pattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public int repetitions() {
            return repetitions;
        }

        public void repetitions(int repetitions) {
            this.repetitions = repetitions;
        }

        @Override
        public String toString() {
            return String.format("%s * %d", pattern.name(), repetitions);
        }
    }
}
