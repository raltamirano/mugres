package mugres.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Arrangement {
    private final List<Entry> entries = new ArrayList<>();

    public void append(final Section section, final int repetitions) {
        entries.add(new Entry(section, repetitions));
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public static class Entry {
        private Section section;
        private int repetitions;

        public Entry(Section section, int repetitions) {
            this.section = section;
            this.repetitions = repetitions;
        }

        public Section section() {
            return section;
        }

        public void section(Section section) {
            this.section = section;
        }

        public int repetitions() {
            return repetitions;
        }

        public void repetitions(int repetitions) {
            this.repetitions = repetitions;
        }

        @Override
        public String toString() {
            return String.format("%s * %d", section, repetitions);
        }
    }
}
