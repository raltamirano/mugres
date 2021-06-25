package mugres.core.common.euclides;

public class EuclideanPattern {
    private final int size;
    private final int events;
    private final int offset;
    private final int[] pattern;

    private EuclideanPattern(final int size, final int events, final int offset) {
        if (size <= 0)
            throw new IllegalArgumentException("Size must be > 0");
        if (events < 0)
            throw new IllegalArgumentException("Events must be >= 0");
        if (events > size)
            throw new IllegalArgumentException("Events must be <= Size");

        this.size = size;
        this.events = events;
        this.offset = offset;

        pattern = calculatePattern();
    }

    public static EuclideanPattern of(final int size, final int events) {
        return of(size, events, 0);
    }

    public static EuclideanPattern of(final int size, final int events, final int offset) {
        return new EuclideanPattern(size, events, offset);
    }

    public int[] pattern() {
        return pattern;
    }

    public boolean eventAt(final int position) {
        return pattern[position % size] == 1;
    }

    private int[] calculatePattern() {
        final int[] pattern = new int[size];

        int bucket = 0;
        for(int i=0; i<size; i++) {
            bucket += events;

            if (bucket >= size) {
                bucket -= size;
                pattern[i] = 1;
            } else if (bucket < size)
                pattern[i] = 0;
        }

        return rotate(pattern, offset + 1);
    }

    private int[] rotate(final int[] pattern, final int offset) {
        final int[] rotated = new int[pattern.length];
        final int val = pattern.length - offset;

        for(int i=0; i<pattern.length; i++)
            rotated[i] = pattern[Math.abs((i+val) % pattern.length)];

        return rotated;
    }
}
