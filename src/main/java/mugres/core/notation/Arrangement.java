package mugres.core.notation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Arrangement {
    private final List<Entry> entries = new ArrayList<>();

    public void addEntry(final Section section, final int repetitions) {
        entries.add(new Entry(section, repetitions));
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public static class Entry {
        private Section section;
        private int repetitions;

        public Entry(Section section, int repetitions) {
            this.section = section;
            this.repetitions = repetitions;
        }

        public Section getSection() {
            return section;
        }

        public void setSection(Section section) {
            this.section = section;
        }

        public int getRepetitions() {
            return repetitions;
        }

        public void setRepetitions(int repetitions) {
            this.repetitions = repetitions;
        }

        @Override
        public String toString() {
            return String.format("%s * %d", section, repetitions);
        }
    }

}
