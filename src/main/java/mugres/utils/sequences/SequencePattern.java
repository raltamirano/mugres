package mugres.utils.sequences;

import java.util.Collections;
import java.util.List;

public class SequencePattern {
    private final List<Integer> steps;
    private final int offset;

    private SequencePattern(final List<Integer> steps, final int offset) {
        if (steps == null || steps.isEmpty())
            throw new IllegalArgumentException("steps");
        if (offset < 0)
            throw new IllegalArgumentException("offset");

        this.steps = steps;
        this.offset = offset;
    }

    public static SequencePattern of(final List<Integer> steps, final int offset) {
        return new SequencePattern(steps, offset);
    }

    public List<Integer> steps() {
        return Collections.unmodifiableList(steps);
    }

    public int offset() {
        return offset;
    }

    public int maxStep() {
        return steps.stream().max(Integer::compareTo).orElse(-1);
    }
}
