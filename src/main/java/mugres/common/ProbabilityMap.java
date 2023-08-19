package mugres.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ProbabilityMap<T> {
    private static int MIN = 0;
    private static int MAX = 10000;
    private final String name;
    private final List<Entry<T>> entries;
    private final Random rnd;

    private ProbabilityMap(final String name, final List<Entry<T>> entries,
                           final Long seed) {
        if (name == null)
            throw new IllegalArgumentException("name");

        validateEntries(entries);

        this.name = name;
        this.entries = new ArrayList<>(entries);
        Collections.sort(this.entries);
        this.rnd = seed == null ? new Random() : new Random(seed);
    }

    public String name() {
        return name;
    }

    public T value() {
        final int random = rnd.nextInt(MAX);
        int accumulator = 0;
        for(int i=0; i<entries.size(); i++) {
            accumulator += entries.get(i).probability;
            if (random < accumulator)
                return entries.get(i).value();
        }

        throw new IllegalStateException("Internal error calculating ProbabilityMap value!");
    }

    public static<X> Builder<X> builder() {
        return builder(UUID.randomUUID().toString());
    }

    public static<X> Builder<X> builder(final String name) {
        return new Builder<>(name);
    }

    private void validateEntries(final List<Entry<T>> entries) {
        if (entries == null || entries.isEmpty())
            throw new IllegalArgumentException("entries");

        if (entries.stream().map(Entry::probability).mapToInt(Integer::intValue).sum() != MAX)
            throw new IllegalArgumentException("Probabilities don't add up to the total!");
    }

    public static class Builder<X> {
        private final String name;
        private final List<Entry<X>> entries = new ArrayList<>();
        private Long seed;

        public Builder(final String name) {
            this.name = name;
        }

        public Builder<X> add(final int probability, final X value) {
            entries.add(new Entry<>(probability, value));
            return this;
        }

        public Builder<X> add(final double probability, final X value) {
            entries.add(new Entry<>((int)(probability * 10_000), value));
            return this;
        }

        public Builder<X> seed(Long seed) {
            this.seed = seed;
            return this;
        }

        public ProbabilityMap<X> build() {
            return new ProbabilityMap<>(name, entries, seed);
        }
    }

    private static class Entry<T> implements Comparable<Entry<T>> {
        private final int probability;
        private final T value;

        public Entry(int probability, T value) {
            this.probability = probability;
            this.value = value;
        }

        public int probability() {
            return probability;
        }

        public T value() {
            return value;
        }

        @Override
        public int compareTo(Entry<T> o) {
            return Integer.compare(this.probability, o.probability) * -1;
        }
    }
}
