package mugres.tracker;

import mugres.parametrizable.ParametrizableSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptySet;

public class Arrangement extends TrackerElement {
    public static final String ENTRIES = "entries";

    private final List<Entry> entries = new ArrayList<>();
    private final Song song;

    private Arrangement(final UUID id, final String name, final Song song) {
        super(id, name, null);

        if (song == null)
            throw new IllegalArgumentException("song");

        this.song = song;
    }


    @Override
    protected ParametrizableSupport createParametrizableSupport() {
        return ParametrizableSupport.forTarget(emptySet(), this);
    }

    public static Arrangement of(final Song song) {
        if (song == null)
            throw new IllegalArgumentException("song");

        return new Arrangement(UUID.randomUUID(), "Arrangement for song: " + song.id(), song);
    }

    public void append(final Pattern pattern, final int repetitions) {
        entries.add(Entry.of(this, pattern, repetitions));
        propertyChangeSupport().firePropertyChange(ENTRIES, null, entries());
    }

    public void remove(final int index) {
        if (index < 0 || index >= entries.size())
            return;

        entries.remove(index);
        propertyChangeSupport().firePropertyChange(ENTRIES, null, entries());
    }

    public void moveBack(final int index) {
        if (index <= 0 || index >= entries.size())
            return;

        Collections.swap(entries, index, index - 1);
        propertyChangeSupport().firePropertyChange(ENTRIES, null, entries());
    }

    public void moveForward(final int index) {
        if (index < 0 || index >= entries.size() - 1)
            return;

        Collections.swap(entries, index, index + 1);
        propertyChangeSupport().firePropertyChange(ENTRIES, null, entries());
    }

    public boolean removeAllForPattern(Pattern toRemove) {
        final boolean removed = entries.removeIf(e -> e.pattern().equals(toRemove));
        if (removed)
            propertyChangeSupport().firePropertyChange(ENTRIES, null, entries());
        return removed;
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public String toString() {
        return "Arrangement{" +
                "entries=" + entries +
                '}';
    }

    public static class Entry {
        private final Arrangement arrangement;
        private Pattern pattern;
        private int repetitions;

        private Entry(final Arrangement arrangement, final Pattern pattern, final int repetitions) {
            if (arrangement == null)
                throw new IllegalArgumentException("arrangement");
            if (pattern == null)
                throw new IllegalArgumentException("pattern");
            if (repetitions <= 0)
                throw new IllegalArgumentException("repetitions");

            this.arrangement = arrangement;
            this.pattern = pattern;
            this.repetitions = repetitions;
        }

        public static Entry of(final Arrangement arrangement, Pattern pattern, int repetitions) {
            return new Entry(arrangement, pattern, repetitions);
        }

        public Arrangement arrangement() {
            return arrangement;
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
