package mugres.core.common.euclides;

public class EuclideanPattern {
    private final int steps;
    private final int events;
    private final int offset;
    private final int[] pattern;

    private EuclideanPattern(final int steps, final int events, final int offset) {
        if (steps <= 0)
            throw new IllegalArgumentException("Steps must be > 0");
        if (events < 0)
            throw new IllegalArgumentException("Events must be >= 0");
        if (events > steps)
            throw new IllegalArgumentException("Events must be <= Size");

        this.steps = steps;
        this.events = events;
        this.offset = offset;

        pattern = calculatePattern();
    }

    public static EuclideanPattern of(final int steps, final int events) {
        return of(steps, events, 0);
    }

    public static EuclideanPattern of(final int steps, final int events, final int offset) {
        return new EuclideanPattern(steps, events, offset);
    }

    public int steps() {
        return steps;
    }

    public int events() {
        return events;
    }

    public int offset() {
        return offset;
    }

    public int[] pattern() {
        return pattern;
    }

    public boolean eventAt(final int position) {
        return pattern[position % steps] == 1;
    }

    private int[] calculatePattern() {
        final int[] pattern = new int[steps];

        int bucket = 0;
        for(int s = 0; s < steps; s++) {
            bucket += events;

            if (bucket >= steps) {
                bucket -= steps;
                pattern[s] = 1;
            } else if (bucket < steps)
                pattern[s] = 0;
        }

        return rotate(pattern, offset + 1);
    }

    private int[] rotate(final int[] pattern, final int offset) {
        final int[] rotated = new int[pattern.length];
        final int val = pattern.length - offset;

        for(int s = 0; s < pattern.length; s++)
            rotated[s] = pattern[Math.abs((s+val) % pattern.length)];

        return rotated;
    }
}
