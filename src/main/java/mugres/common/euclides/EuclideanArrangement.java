package mugres.common.euclides;

import mugres.common.Instrument;
import mugres.common.Pitch;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static mugres.common.MIDI.isValidVelocity;

public class EuclideanArrangement {
    private final List<Entry> patterns = new ArrayList<>();

    private EuclideanArrangement() {}

    public static EuclideanArrangement of() {
        return new EuclideanArrangement();
    }

    public EuclideanArrangement add(final Instrument instrument,
                                    final Pitch pitch,
                                    final int velocity,
                                    EuclideanPattern pattern) {
        if (patterns.size() >= MAX_PATTERNS)
            throw new IllegalStateException("Max patterns permitted: " + MAX_PATTERNS);

        patterns.add(new Entry(instrument, pitch, velocity, pattern));

        return this;
    }

    public List<Entry> patterns() {
        return unmodifiableList(patterns);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        patterns.forEach(p -> builder
                .append(p.instrument).append("/")
                .append(p.pitch).append("/")
                .append(p.velocity).append(System.lineSeparator())
                .append(p.pattern)
                .append(System.lineSeparator())
        );

        return builder.toString();
    }

    public static class Entry {
        private final Instrument instrument;
        private final Pitch pitch;
        private final int velocity;
        private final EuclideanPattern pattern;

        Entry(final Instrument instrument, final Pitch pitch,
              final int velocity, final EuclideanPattern pattern) {

            if (instrument == null)
                throw new IllegalArgumentException("instrument");
            if (pitch == null)
                throw new IllegalArgumentException("pitch");
            if (!isValidVelocity(velocity))
                throw new IllegalArgumentException("velocity");
            if (pattern == null)
                throw new IllegalArgumentException("pattern");

            this.instrument = instrument;
            this.pitch = pitch;
            this.velocity = velocity;
            this.pattern = pattern;
        }

        public Instrument instrument() {
            return instrument;
        }

        public Pitch pitch() {
            return pitch;
        }

        public int velocity() {
            return velocity;
        }

        public EuclideanPattern pattern() {
            return pattern;
        }
    }

    private static final int MAX_PATTERNS = 16;
}
