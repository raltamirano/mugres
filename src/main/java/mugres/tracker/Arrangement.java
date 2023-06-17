package mugres.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Arrangement {
    private final List<Entry> entries = new ArrayList<>();

    public void append(final Pattern pattern, final int repetitions) {
        entries.add(new Entry(pattern, repetitions));
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
            return String.format("%s * %d", pattern, repetitions);
        }
    }
}
